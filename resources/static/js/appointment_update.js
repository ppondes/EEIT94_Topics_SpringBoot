$(document).ready(function () {
	let today = new Date().toISOString().split("T")[0];
	   $('#datepicker').datepicker({
	       format: 'yyyy-mm-dd',
	       autoclose: true,
	       todayHighlight: true,
		   startDate: today
	   }).on("changeDate", function (e) {
	       var selectedDate = $('#appointmentDate').val();
	       console.log("選擇的日期是: " + selectedDate);
	       if (!selectedDate) {
	           alert("請選擇一個日期");
	           return;
	       }

	       $.ajax({
	           url: "/api/appointment/queryBookingTime",
	           type: "GET",
	           data: { appointmentDate: selectedDate },
	           dataType: "json",
	           success: function (response) {
	               console.log(response);
	               var bookedTimeslots = response.bookedTimeslots || [];
	               var allTimeslots = ["10:00-12:00", "12:00-14:00", "14:00-16:00", "16:00-18:00", "18:00-20:00"];
	               var timeslotSelect = $("#appointmentTimeslot");
	               timeslotSelect.empty();
	               allTimeslots.forEach(function (slot) {
	                   var option = $("<option></option>").val(slot).text(slot);
	                   if (bookedTimeslots.includes(slot)) {
	                       option.prop("disabled", true);
	                   }
	                   timeslotSelect.append(option);
	               });
	           },
	           error: function (xhr, status, error) {
	               console.error("無法獲取預約資料: " + error);
	           }
	       });
	   });

    $('#updateButton').click(function (e) {
        e.preventDefault();
        const formDataObj = {
            appointmentId: $('#appointmentId').val(),
            appointmentDate: $('#appointmentDate').val(),
            appointmentTimeslot: $('#appointmentTimeslot').val(),
            appointmentStatus: $('#appointmentStatus').val(),
            paymentStatus: $('#paymentStatus').val(),
            appointmentTotal: $('#appointmentTotal').val(),
            services: $('#serviceSelect').val(),
            extraPackages: $('input[name="extraPackages"]:checked').map(function () {
                return parseInt($(this).val());
            }).get()
        };
        console.log('Selected service:', $('#serviceSelect').val());
        $.ajax({
            url: '/api/appointment/update/' + appointmentId,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            success: function (response) {
                alert("更新成功：" + response.message);
                window.location.href = "/appointment/result/Appointmnet";
            },
            error: function (xhr, status, error) {
                console.error("發生錯誤：", error);
                alert("更新失敗，請稍後再試");
            }
        });
    });

    function calculateTotalPrice() {
        let total = 0;
        let servicePrice = $("#serviceSelect option:selected").data("price") || 0;
        total += servicePrice;
        $("input[name='extraPackages']:checked").each(function () {
            total += parseInt($(this).data("price")) || 0;
        });
        $("#totalPrice2").text("總價: " + total + " 元");
        $("#totalPrice").val(total);
    }
    calculateTotalPrice();
    $(document).on("change", "#serviceSelect, input[name='extraPackages']", calculateTotalPrice);
});