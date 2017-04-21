package com.geekhub.services.Booking;

import com.geekhub.domain.entities.Booking;
import com.geekhub.repositories.Booking.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;

    @Autowired
    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking findOne(Long id) {
        return bookingRepository.findOne(id);
    }

    @Override
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.getUserBookings(userId);
    }

    @Override
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    @Override
    public void delete(Long id) {
        bookingRepository.delete(id);
    }

    @Override
    public Set<LocalDate> getReservedDays(Long roomId) {
        return bookingRepository.getReservedDays(roomId);
    }

    //    public List<LocalDate> getBookingDays(Booking booking) {
//        LocalDate start = booking.getBeginDate();
//        LocalDate end = booking.getEndDate();
//        List<LocalDate> dates = new ArrayList<>();
//        Stream.iterate(start, date -> date.plusDays(1))
//                .limit(ChronoUnit.DAYS.between(start, end) + 1)
//                .forEach(dates::add);
//        return dates;
//    }
//
//    public User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails myUser = (CustomUserDetails) authentication.getPrincipal();
//        return myUser.getUser();
//    }

//    public List<Room> getAvailableRooms(int numberRooms, Hotel hotel, RoomType roomType, Booking booking, List<LocalDate> dates) {
//        Map<Long, Room> roomsFromHotel = hotel.getRooms();
//        List<Room> availableRooms = new ArrayList<>();
//        int counter = 1;
//        for (Long hotelRoomsId : roomsFromHotel.keySet()) {
//            Room room = roomsFromHotel.get(hotelRoomsId);
//            Map<LocalDate, Long> roomBookings = room.getReservedDays();
//            boolean found = false;
//            for (LocalDate day : dates) {
//                if (roomBookings.get(day) != null) {
//                    found = true;
//                    break;
//                }
//            }
//            if ((!found) && (room.getType() == roomType) && (counter <= numberRooms)) {
//                availableRooms.add(room);
//                for (LocalDate date : dates) {
//                    roomBookings.put(date, booking.getId());
//                }
//                counter++;
//            } else if (counter > numberRooms) {
//                break;
//            }
//        }
//        return availableRooms;
//    }
//
//    public List<Room> getAvailableRooms(int numberRooms, RoomType roomType, List<LocalDate> dates) {
//        List<Room> availableRooms = new ArrayList<>();
//        for (Hotel hotel : hotels.findAll()) {
//            if (hotel.getStatus()) {
//                Map<Long, Room> rooms = hotel.getRooms();
//                int counter = 0;
//                Room currentRoom = null;
//                for (Map.Entry<Long, Room> roomsEntry : rooms.entrySet()) {
//                    Room room = roomsEntry.getValue();
//                    Map<LocalDate, Long> roomBookings = room.getReservedDays();
//                    boolean found = false;
//                    for (LocalDate day : dates) {
//                        if (roomBookings.get(day) != null) {
//                            found = true;
//                            break;
//                        }
//                    }
//                    if (!found && room.getType().getDescription().equals(roomType.getDescription())) {
//                        counter++;
//                        currentRoom = room;
//                    }
//                }
//                if (counter >= numberRooms) {
//                    availableRooms.add(currentRoom);
//                }
//            }
//        }
//        return availableRooms;
//    }
}
