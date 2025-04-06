$(document).ready(function () {

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
            url: '/appointment/appointment_update/' + appointmentId,
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