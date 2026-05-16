package com.deepak.AirBnB.service.impl;

import com.deepak.AirBnB.dto.*;
import com.deepak.AirBnB.entity.*;
import com.deepak.AirBnB.enums.BookingStatus;
import com.deepak.AirBnB.exception.ResourceNotFoundException;
import com.deepak.AirBnB.exception.UnAuthorisedException;
import com.deepak.AirBnB.repository.*;
import com.deepak.AirBnB.service.BookingService;
import com.deepak.AirBnB.service.CheckoutService;
import com.deepak.AirBnB.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.deepak.AirBnB.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;
    private final InventoryRepository inventoryRepository;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initBooking(BookingRequest bookingRequest) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Initialising booking for hotel : {}, room: {}, date {}-{}", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());
        Hotel hotel= hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+bookingRequest.getHotelId()));
        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+bookingRequest.getRoomId()));

        List<Inventory> inventoryList= inventoryRepository.findAndLockAvailableInventory(bookingRequest.getRoomId()
                ,bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());
        long dayCount= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;
        if(inventoryList.size()!=dayCount) {
        throw new IllegalStateException("Room is not available anymore");
        }
        // Reserve the room/ update the booked count of inventories
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());
        BigDecimal priceForOneRoom=pricingService.calculatePriceForBooking(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        inventoryRepository.saveAll(inventoryList);
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(user)
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("No Booking found with this Id "+bookingId));
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }
        if(isBookingExpired(booking)){
            throw new IllegalStateException("Booking is expired");
        }
        if(booking.getBookingStatus()!=BookingStatus.RESERVED){
            throw new IllegalStateException("Booking status is not RESERVED, cannot add guests");
        }
        for(GuestDto guestDto: guestDtoList) {
            Guest guest=modelMapper.map(guestDto,Guest.class);
            guest.setUser(getCurrentUser());
            guest=guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking=bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No Booking found with this Id " + bookingId));
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: " + user.getId());
        }
        if (isBookingExpired(booking)) {
            throw new IllegalStateException("Booking is expired");
        }

        String sessionUrl= checkoutService.getCheckoutUrl(booking, frontendUrl+"/payments/" +bookingId +"/status",
                frontendUrl+"/payments/" +bookingId +"/status");

        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session= (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(session == null) return;
            String sessionId = session.getId();
            Booking booking =bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                    new ResourceNotFoundException("Booking not found for session ID: "+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),
                    booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(),
                    booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
        }
        else{
            log.warn("Unhandled event type: {}", event.getType());
        }

    }

    @Override
    @Transactional
    public void cancelPayment(Long bookingId) {
        Booking booking =bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found for session ID: "+bookingId));
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: " + user.getId());
        }
        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        try{
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams params= RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(params);
        }
        catch (StripeException e){
        throw new RuntimeException(e);
        }

    }

    @Override
    public List<BookingDto> getAllBookingsByHotelId(Long hotelId) {
        log.info("getting All Bookings for hotel with id {}", hotelId);
        User user =getCurrentUser();
        Hotel hotel =hotelRepository.findById(hotelId)
                .orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+hotelId));
        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotelId);
        }
        List<Booking> bookings= bookingRepository.findByHotel(hotel);

        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        log.info("getting Hotel Report for hotel with id {}", hotelId);
        User user =getCurrentUser();
        Hotel hotel =hotelRepository.findById(hotelId)
                .orElseThrow(()->new RuntimeException("No Hotel Found with this Id "+hotelId));
        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotelId);
        }
       LocalDateTime startDateTime = startDate.atStartOfDay();
       LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<Booking> bookings= bookingRepository.findByHotelAndCreatedAtBetween(hotel,startDateTime,endDateTime);
        Long totalBookings= bookings.stream()
                .filter(booking -> booking.getBookingStatus()==BookingStatus.CONFIRMED).count();
        BigDecimal totalRevenue= bookings.stream().filter(booking -> booking.getBookingStatus()==BookingStatus.CONFIRMED)
                .map(Booking::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgRevenue= totalBookings==0 ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(totalBookings), RoundingMode.HALF_UP);

        return new HotelReportDto(totalBookings,totalRevenue,avgRevenue);
    }

    @Override
    public List<BookingDto> getMyBookings() {

        User user =getCurrentUser();
        return bookingRepository.findByUser(user)
                .stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking =bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found for session ID: "+bookingId));
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: " + user.getId());
        }
        return booking.getBookingStatus().toString();
    }

    public boolean isBookingExpired(Booking booking) {
        return  booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
}
