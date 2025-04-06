package com.topics.appointment.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topics.appointment.model.bean.Appointment;
import com.topics.appointment.model.bean.ItemDetails;
import com.topics.appointment.model.bean.Items;
import com.topics.appointment.model.bean.Owner;
import com.topics.appointment.model.bean.PackageDetails;
import com.topics.appointment.model.bean.Pet;
import com.topics.appointment.model.bean.ServicePackage;

import io.micrometer.common.util.StringUtils;

@Service
public class AppointmentService {
	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private ItemsRepository itemsRepository;

	@Autowired
	private ServicePackageRepository servicePackageRepository;

	@Autowired
	private ItemDetailsRepository itemDetailsRepository;

	@Autowired
	private PackageDetailsRepository packageDetailsRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	public List<Appointment> getAllAppointments() {
		List<Object[]> result = appointmentRepository.findAllAppointments();
		List<Appointment> appointments = new ArrayList<>();
		for (Object[] row : result) {
			Appointment appointment = new Appointment();
			appointment.setAppointmentId((Integer) row[0]);
			Owner owner = new Owner();
			owner.setMemberId((Integer) row[1]);
			appointment.setOwner(owner);
			Pet pet = new Pet();
			pet.setPetId((Integer) row[2]);
			appointment.setPet(pet);
			appointment.setAppointmentDate(row[3] != null ? row[3].toString() : null);
			appointment.setAppointmentTimeslot((String) row[4]);
			appointment.setAppointmentTotal((Integer) row[5]);
			appointment.setAppointmentStatus(((Number) row[6]).intValue());
			appointment.setPaymentStatus(((Number) row[7]).intValue());
			appointment.setServiceNames((String) row[8]);
			String additionalPackages = (String) row[9];
			appointment.setAdditionalPackages(StringUtils.isBlank(additionalPackages) ? "無加購服務" : additionalPackages);
			appointments.add(appointment);
		}
		return appointments;
	}
	
	@Transactional
	public int insertAppointment(Appointment appointment) {
		appointment.setAppointmentTotal(0);
		appointment.setAppointmentStatus(0);
		appointment.setPaymentStatus(0);
		appointment = appointmentRepository.save(appointment);
		System.out.println("Saved Appointment ID: " + appointment.getAppointmentId());
		return appointment.getAppointmentId();
	}

	@Transactional
	public void addServiceToAppointment(int appointmentId, int serviceId, int quantity) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new IllegalArgumentException("Appointment 不存在！"));
		Items service = itemsRepository.findById(serviceId)
				.orElseThrow(() -> new IllegalArgumentException("Service 不存在！"));
		ItemDetails itemDetails = new ItemDetails();
		itemDetails.setAppointment(appointment);
		itemDetails.setItem(service);
		itemDetails.setItemDetailQuantity(quantity);
		itemDetailsRepository.save(itemDetails);
	}

	@Transactional
	public void addExtraPackageToAppointment(int appointmentId, int packageId, int quantity) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new IllegalArgumentException("Appointment 不存在！"));
		ServicePackage servicePackage = servicePackageRepository.findById(packageId)
				.orElseThrow(() -> new IllegalArgumentException("ServicePackage 不存在！"));
		PackageDetails packageDetails = new PackageDetails();
		packageDetails.setAppointment(appointment);
		packageDetails.setServicePackage(servicePackage);
		packageDetails.setPackageDetailsQuantity(quantity);
		packageDetailsRepository.save(packageDetails);
	}

	@Transactional
	public void updateAppointmentPrice(int appointmentId, int totalPrice) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new IllegalArgumentException("No appointment found with ID: " + appointmentId));
		appointment.setAppointmentTotal(totalPrice);
		appointmentRepository.save(appointment);
	}

	@Transactional
	public List<Appointment> searchAppointmentsByPhoneNumber(String phoneNumber) {
		List<Object[]> result = appointmentRepository.findAppointmentsByPhoneNumber(phoneNumber);
		List<Appointment> appointments = new ArrayList<>();
		for (Object[] row : result) {
			Appointment appointment = new Appointment();
			appointment.setAppointmentId((Integer) row[0]);
			Owner owner = new Owner();
			owner.setMemberId((Integer) row[1]);
			appointment.setOwner(owner);
			Pet pet = new Pet();
			pet.setPetId((Integer) row[2]);
			appointment.setPet(pet);
			appointment.setAppointmentDate(row[3] != null ? row[3].toString() : null);
			appointment.setAppointmentTimeslot((String) row[4]);
			appointment.setAppointmentTotal((Integer) row[5]);
			appointment.setAppointmentStatus(((Number) row[6]).intValue());
			appointment.setPaymentStatus(((Number) row[7]).intValue());
			appointment.setServiceNames((String) row[8]);
			String additionalPackages = (String) row[9];
			appointment.setAdditionalPackages(StringUtils.isBlank(additionalPackages) ? "無加購服務" : additionalPackages);
			appointments.add(appointment);
		}
		return appointments;
	}

	@Transactional
	public boolean deleteAppointment(int appointmentId) {
		if (!appointmentRepository.existsById(appointmentId)) {
			throw new IllegalArgumentException("No appointment found with ID: " + appointmentId);
		}
		packageDetailsRepository.deleteByAppointmentId(appointmentId);
		itemDetailsRepository.deleteByAppointmentId(appointmentId);
		appointmentRepository.deleteById(appointmentId);

		return true;
	}


	@Transactional(readOnly = true)
	public Appointment getAppointmentById(int appointmentId) {
		return appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new IllegalArgumentException("No appointment found with ID: " + appointmentId));
	}

	@Transactional(readOnly = true)
	public Owner getOwnerById(int memberId) {
		return ownerRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("No owner found with ID: " + memberId));
	}

	@Transactional
	public boolean updateAppointment(Appointment appointment, List<ItemDetails> itemDetails,
			List<PackageDetails> packageDetails) {
		Appointment existingAppointment = appointmentRepository.findById(appointment.getAppointmentId()).orElseThrow(
				() -> new IllegalArgumentException("No appointment found with ID: " + appointment.getAppointmentId()));
		existingAppointment.setAppointmentDate(appointment.getAppointmentDate());
		existingAppointment.setAppointmentTimeslot(appointment.getAppointmentTimeslot());
		existingAppointment.setAppointmentTotal(appointment.getAppointmentTotal());
		existingAppointment.setAppointmentStatus(appointment.getAppointmentStatus());
		existingAppointment.setPaymentStatus(appointment.getPaymentStatus());
		itemDetailsRepository.deleteByAppointmentId(appointment.getAppointmentId());
	    if (itemDetails != null && !itemDetails.isEmpty()) {
	        for (ItemDetails itemDetail : itemDetails) {
	            ItemDetails existingItemDetail = itemDetailsRepository.findByAppointmentIdAndItemId(appointment.getAppointmentId(), itemDetail.getItemId());
	            if (existingItemDetail != null) {
	                existingItemDetail.setItemDetailQuantity(itemDetail.getItemDetailQuantity());
	                existingItemDetail.setItem(itemDetail.getItem());
	                existingItemDetail.setAppointment(existingAppointment);
	                System.out.println("Updated item detail: " + existingItemDetail);
	            } else {
	                itemDetail.setAppointment(existingAppointment);
	                System.out.println("Inserting new item detail: " + itemDetail);
	                itemDetailsRepository.save(itemDetail);
	            }
	        }
	    }
	    packageDetailsRepository.deleteByAppointmentId(appointment.getAppointmentId());
	    if (packageDetails != null && !packageDetails.isEmpty()) {
	        for (PackageDetails packageDetail : packageDetails) {
	            PackageDetails existingPackageDetail = packageDetailsRepository
	                    .findByAppointmentIdAndPackageId(appointment.getAppointmentId(), packageDetail.getPackageId());
	            if (existingPackageDetail != null) {
	                existingPackageDetail.setPackageDetailsQuantity(packageDetail.getPackageDetailsQuantity());
	                existingPackageDetail.setServicePackage(packageDetail.getServicePackage());
	                existingPackageDetail.setAppointment(existingAppointment);
	                System.out.println("Updated package detail: " + existingPackageDetail);
	            } else {
	                packageDetail.setAppointment(existingAppointment);
	                System.out.println("Inserting new package detail: " + packageDetail);
	                packageDetailsRepository.save(packageDetail);
	            }
	        }
	    }
	    existingAppointment.setItems(itemDetails);
	    existingAppointment.setPackages(packageDetails);
	    System.out.println("Saving updated appointment: " + existingAppointment);
		appointmentRepository.save(existingAppointment);
		return true;
	}

	public ItemDetails getServiceById(int appointmentId) {
		return itemDetailsRepository.findByAppointmentId(appointmentId);
	}

	public List<String> getServicesByAppointmentId(int appointmentId) {
		return itemDetailsRepository.findServiceNamesByAppointmentId(appointmentId);
	}

	public List<Integer> getSelectedExtraPackages(int appointmentId) {
		return packageDetailsRepository.findPackageIdsByAppointmentId(appointmentId);
	}
	
	public List<ItemDetails> getItemSelectedIds(String[] services) {
        List<ItemDetails> itemDetails = new ArrayList<>();
        if (services != null && services.length > 0) {
            for (String serviceId : services) {
                int id = Integer.parseInt(serviceId);

                Items item = itemsRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + id));

                ItemDetails itemDetail = new ItemDetails();
                itemDetail.setItem(item);               
                itemDetail.setItemId(id);               
                itemDetail.setItemDetailQuantity(1);    
                itemDetails.add(itemDetail);
            }
        }
        return itemDetails;
    }

	public List<PackageDetails> getPackageSelectedIds(String[] extraPackages) {
	    List<PackageDetails> packageDetails = new ArrayList<>();
	    if (extraPackages != null && extraPackages.length > 0) {
	        for (String packageId : extraPackages) {
	            int id = Integer.parseInt(packageId);
	            ServicePackage servicePackage = servicePackageRepository.findById(id)
	                    .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found with ID: " + id));
	            PackageDetails packageDetail = new PackageDetails();
	            packageDetail.setPackageId(id);
	            packageDetail.setServicePackage(servicePackage); 
	            packageDetails.add(packageDetail);
	        }
	    }
	    return packageDetails;
	}
	
	public boolean isServiceAlreadyAdded(int appointmentId, int serviceId) {
		return itemDetailsRepository.existsByAppointment_AppointmentIdAndItem_ItemId(appointmentId, serviceId);
	}

	public List<String> getBookedTimeslots(String appointmentDate) {
		return appointmentRepository.findBookedTimeslotsByDate(appointmentDate);
	}

	public boolean isTimeslotBooked(String appointmentDate, String appointmentTimeslot) {
		return appointmentRepository.existsByAppointmentDateAndAppointmentTimeslot(appointmentDate,
				appointmentTimeslot);
	}

}