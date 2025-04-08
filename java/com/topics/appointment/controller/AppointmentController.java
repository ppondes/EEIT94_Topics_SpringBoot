package com.topics.appointment.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.topics.appointment.model.bean.Appointment;
import com.topics.appointment.model.bean.ItemDetails;
import com.topics.appointment.model.bean.Owner;
import com.topics.appointment.model.bean.PackageDetails;
import com.topics.appointment.model.bean.Pet;
import com.topics.appointment.model.service.AppointmentService;
import com.topics.appointment.model.service.PetService;
import com.topics.appointment.model.service.PricingService;

@CrossOrigin(origins = "http://localhost:8080")
@Controller
@RequestMapping("/api/appointment")
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private PetService petService;
	
	@Autowired
	private PricingService pricingService;


	@GetMapping("/appointment_home")
	public String showAppointmentPage(Model model) {
		List<Appointment> appointments = appointmentService.getAllAppointments();
		for (Appointment appointment : appointments) {
			Set<String> uniqueServices = new HashSet<>();
			String serviceNames = appointment.getServiceNames();

			if (serviceNames != null && !serviceNames.isEmpty()) {
				String[] services = serviceNames.split(",");
				for (String service : services) {
					uniqueServices.add(service.trim());
				}
				appointment.setServiceNames(String.join(", ", uniqueServices));
			} else {
				appointment.setServiceNames("無服務資料");
			}
		}
		if (appointments == null || appointments.isEmpty()) {
			System.out.println("沒有找到任何預約資料");
		}
		model.addAttribute("appointments", appointments);
		return "appointment/home/Appointment";
	}

	@GetMapping("/appointment_query")
	public String selectAppointmentByPhoneNum(@RequestParam(name = "phone_number", required = false) String phoneNumber,
			Model model) {
		List<Appointment> appointments = appointmentService.searchAppointmentsByPhoneNumber(phoneNumber);
		for (Appointment appointment : appointments) {
			Set<String> uniqueServices = new HashSet<>();
			String serviceNames = appointment.getServiceNames();
			if (serviceNames != null && !serviceNames.isEmpty()) {
				String[] services = serviceNames.split(",");
				for (String service : services) {
					uniqueServices.add(service.trim());
				}
				appointment.setServiceNames(String.join(", ", uniqueServices));
			} else {
				appointment.setServiceNames("無服務資料");
			}
		}
		if (appointments == null || appointments.isEmpty()) {
			System.out.println("沒有找到任何預約資料");
		}
		model.addAttribute("phoneNumber", phoneNumber);
		model.addAttribute("appointments", appointments);
		return "appointment/query/Appointment";
	}

	@GetMapping("/queryBookingTime")
	public ResponseEntity<Map<String, Object>> handleQueryBookingTime(@RequestParam String appointmentDate) {
		List<String> bookedTimeslots = appointmentService.getBookedTimeslots(appointmentDate);
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("bookedTimeslots", bookedTimeslots);
		return ResponseEntity.ok(responseData);
	}

	@GetMapping("/querypet")
	public ResponseEntity<Object> handleQueryPetById(@RequestParam("memberId") String memberIdStr) {
		if (memberIdStr == null || memberIdStr.trim().isEmpty()) {
			return ResponseEntity.badRequest().body("memberId 不能為空");
		}
		int memberId;
		try {
			memberId = Integer.parseInt(memberIdStr);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().body("memberId 格式錯誤");
		}
		List<Pet> pets = petService.getPetsByMemberId(memberId);
		System.out.println("查詢到的寵物：" + pets);
		if (pets.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		List<Map<String, Object>> petList = new ArrayList<>();
		for (Pet pet : pets) {
			Map<String, Object> petData = new HashMap<>();
			petData.put("petId", pet.getPetId());
			petData.put("petName", pet.getPetName());
			petList.add(petData);
		}
		return ResponseEntity.ok(petList);
	}

	@PostMapping("/add")
	public String insertAppointmentById(@RequestParam("memberId") String memberIdStr,
			@RequestParam("appointmentpetId") String petIdStr, @RequestParam("appointmentDate") String appointmentDate,
			@RequestParam("appointmentTimeslot") String appointmentTimeslot,
			@RequestParam(value = "services", required = false) String selectedService,
			@RequestParam(value = "extraPackages", required = false) String[] selectedExtras, Model model) {

		if (memberIdStr == null || memberIdStr.trim().isEmpty()) {
			model.addAttribute("success", false);
			model.addAttribute("message", "memberId 不能為空");
			return "appointment/result/Appointment";
		}
		if (petIdStr == null || petIdStr.trim().isEmpty()) {
			model.addAttribute("success", false);
			model.addAttribute("message", "petId 不能為空");
			return "appointment/result/Appointment";
		}
		if (appointmentDate == null || appointmentDate.trim().isEmpty() || appointmentTimeslot == null
				|| appointmentTimeslot.trim().isEmpty()) {
			model.addAttribute("success", false);
			model.addAttribute("message", "日期或時段不能為空");
			return "appointment/result/Appointment";
		}
		int memberId, petId;
		try {
			memberId = Integer.parseInt(memberIdStr);
			petId = Integer.parseInt(petIdStr);
		} catch (NumberFormatException e) {
			model.addAttribute("success", false);
			model.addAttribute("message", "memberId 或 petId 格式錯誤");
			return "appointment/result/Appointment";
		}
		Owner owner = appointmentService.getOwnerById(memberId);
		Pet pet = petService.getPetById(petId);
		if (owner == null) {
			model.addAttribute("success", false);
			model.addAttribute("message", "找不到對應的會員");
			return "appointment/result/Appointment";
		}
		if (appointmentService.isTimeslotBooked(appointmentDate, appointmentTimeslot)) {
			model.addAttribute("success", false);
			model.addAttribute("message", "該時段已被預約，請選擇其他時段！");
			return "appointment/result/Appointment";
		}
		Appointment appointment = new Appointment(owner, pet, appointmentDate, appointmentTimeslot, 0, 0, 0);
		int appointmentId = appointmentService.insertAppointment(appointment);
		int totalPrice = 0;

		if (selectedService != null && !selectedService.isEmpty()) {
			try {
				int serviceId = Integer.parseInt(selectedService);
				int servicePrice = pricingService.getServicePrice(serviceId); 
				totalPrice += servicePrice;
				appointmentService.addServiceToAppointment(appointmentId, serviceId, 1);
			} catch (NumberFormatException e) {
				model.addAttribute("success", false);
				model.addAttribute("message", "無效的服務 ID: " + selectedService);
				return "appointment/result/Appointment";
			}
		}
		if (selectedExtras != null) {
			for (String extraIdStr : selectedExtras) {
				try {
					int extraId = Integer.parseInt(extraIdStr);
					int extraPrice = pricingService.getExtraPackagePrice(extraId);
					totalPrice += extraPrice;
					appointmentService.addExtraPackageToAppointment(appointmentId, extraId, 1);
				} catch (NumberFormatException e) {
					model.addAttribute("success", false);
					model.addAttribute("message", "無效的加購 ID: " + extraIdStr);
					return "appointment/result/Appointment";
				}
			}
		}
		appointmentService.updateAppointmentPrice(appointmentId, totalPrice);
		model.addAttribute("success", true);
		model.addAttribute("appointmentId", appointmentId);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("message", "預約成功！總價為: " + totalPrice + "元");
		return "appointment/result/Appointment";
	}

	@DeleteMapping("/delete/{appointmentId}")
	public ResponseEntity<Map<String, Object>> deleteAppointmentById(@PathVariable int appointmentId) {
		Map<String, Object> response = new HashMap<>();
		boolean isDeleted = appointmentService.deleteAppointment(appointmentId);

		if (isDeleted) {
			response.put("success", true);
			response.put("message", "預約已成功刪除！");
			return ResponseEntity.ok(response);
		} else {
			response.put("success", false);
			response.put("message", "預約刪除失敗！");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/update/{appointmentId}")
	public String showUpdatePage(@PathVariable int appointmentId, Model model) {
		System.out.println("Received request for appointment with ID: " + appointmentId);
		Appointment appointment = appointmentService.getAppointmentById(appointmentId);
		ItemDetails itemdetails = appointmentService.getServiceById(appointmentId);
		List<Integer> extrapackages = appointmentService.getSelectedExtraPackages(appointmentId);
		if (appointment == null) {
			System.out.println("Appointment not found for ID: " + appointmentId);
		} else {
			System.out.println("Appointment found: " + appointment);
		}
		model.addAttribute("appointment", appointment);
		model.addAttribute("itemdetails", itemdetails);
		model.addAttribute("extraPackages", extrapackages);
		System.out.println("Services: " + model.getAttribute("itemdetails"));

		System.out.println("Extra Packages: " + model.getAttribute("extraPackages"));

		return "appointment/update/Appointment";
	}

	@PutMapping("/update/{appointmentId}")
	public String updateAppointmentById(@PathVariable int appointmentId,
			@RequestParam(required = false) String appointmentStatus,
			@RequestParam(required = false) String paymentStatus, @RequestParam String appointmentDate,
			@RequestParam String appointmentTimeslot,
			@RequestParam(value = "services", required = false) String[] services,
			@RequestParam(value = "extraPackages", required = false) String[] extraPackages, Model model) {

		try {
			if (appointmentId <= 0) {
				model.addAttribute("errorMessage", "預約 ID 不能為空或無效。");
				return "appointment/error/Appointment";
			}
			int appointmentStatusValue = (appointmentStatus != null && !appointmentStatus.isEmpty())
					? Integer.parseInt(appointmentStatus)
					: 0;
			int paymentStatusValue = (paymentStatus != null && !paymentStatus.isEmpty())
					? Integer.parseInt(paymentStatus)
					: 0;
			if (appointmentDate == null || appointmentDate.isEmpty()) {
				model.addAttribute("errorMessage", "預約日期不能為空。");
				return "appointment/error/Appointment";
			}
			if (appointmentTimeslot == null || appointmentTimeslot.isEmpty()) {
				model.addAttribute("errorMessage", "預約時段不能為空。");
				return "appointment/error/Appointment";
			}
			int totalPrice = calculateTotalPrice(services, extraPackages);
			Appointment updatedAppointment = new Appointment();
			updatedAppointment.setAppointmentId(appointmentId);
			updatedAppointment.setAppointmentDate(appointmentDate);
			updatedAppointment.setAppointmentTimeslot(appointmentTimeslot);
			updatedAppointment.setAppointmentTotal(totalPrice);
			updatedAppointment.setAppointmentStatus(appointmentStatusValue);
			updatedAppointment.setPaymentStatus(paymentStatusValue);
			List<ItemDetails> itemDetails = appointmentService.getItemSelectedIds(services);
			List<PackageDetails> packageDetails = appointmentService.getPackageSelectedIds(extraPackages);
			System.out.println("services: " + Arrays.toString(services));
			System.out.println("extraPackages: " + Arrays.toString(extraPackages));
			boolean success = appointmentService.updateAppointment(updatedAppointment, itemDetails, packageDetails);
			if (success) {
				model.addAttribute("success", true);
				model.addAttribute("appointmentId", appointmentId);
				model.addAttribute("totalPrice", totalPrice);
				model.addAttribute("message", "預約更新成功！總價為: " + totalPrice + "元");
				return "appointment/result/Appointment"; 
			} else {
				model.addAttribute("errorMessage", "更新失敗，請檢查預約 ID 是否正確。");
				return "appointment/error/Appointment";
			}
		} catch (NumberFormatException e) {
			model.addAttribute("errorMessage", "無效的數字格式。請檢查您輸入的預約 ID、預約狀態或付款狀態。");
			return "appointment/error/Appointment";
		}
	}

	private int calculateTotalPrice(String[] services, String[] extraPackages) {
	    int totalPrice = 0;
	    if (services != null) {
	        for (String serviceIdStr : services) {
	            try {
	                int serviceId = Integer.parseInt(serviceIdStr);
	                totalPrice += pricingService.getServicePrice(serviceId);  
	            } catch (NumberFormatException e) {
	            }
	        }
	    }
	    if (extraPackages != null) {
	        for (String extraIdStr : extraPackages) {
	            try {
	                int extraId = Integer.parseInt(extraIdStr);
	                totalPrice += pricingService.getExtraPackagePrice(extraId);  
	            } catch (NumberFormatException e) {
	            }
	        }
	    }
	    return totalPrice;
	}


}