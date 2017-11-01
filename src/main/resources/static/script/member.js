$(document).ready(function () {
	var id = $('#member_id').val();
	if (id) {
		loadTrainings(id);
	}

	$('#btn_adminEditMember').on("click",function (e) {
		e.preventDefault();

		var formAr = formToObject($('#form'));
		var memberId = $('#member_id').val();
		var csrf = $("[name='_csrf']").val();

		if($.trim(formAr['memberName']) === ""){
			alert("Membername cannot be empty");
		} else if($.trim(formAr.email) === ""){
			alert("Member email cannot be empty");
		} else if ($.trim(formAr.status) === "Select One") {
			alert("Select a status");
		} else {
			if(memberId){
				formAr["id"] = memberId;
			}

			$.ajax({
				headers: { 'X-CSRF-TOKEN': csrf},
				type: "POST",
				contentType: "application/json",
				url: "/members",
				data: JSON.stringify(formAr),
				dataType: 'json',
				timeout: 6000,
				success: function (data) {
					alert("Member Saved");
					window.location.reload();
				}
			});
		}
	});

	$('#btn_editMember').on("click",function (e) {
		e.preventDefault();

		var formAr = formToObject($('#form'));
		var memberId = $('#member_id').val();
		var csrf = $("[name='_csrf']").val();

		if($.trim(formAr.email) === ""){
			alert("Member email cannot be empty");
		} else {
			if(memberId){
				formAr["id"] = memberId;
			}

			$.ajax({
				headers: { 'X-CSRF-TOKEN': csrf},
				type: "POST",
				contentType: "application/json",
				url: "/members/" + memberId,
				data: JSON.stringify(formAr),
				timeout: 6000,
				success: function (data) {
					alert("Member Saved");
					window.location.reload();
				}
			});
		}
	});

	//Complete order button handler
	$('.delete-member').on("click", function(e){
		e.preventDefault();
		if(confirm("Delete?")){
			var Id = $(this).closest("td").attr("id");
			var csrf = $("[name='_csrf']").val();
			$.ajax({
				headers: { 'X-CSRF-TOKEN': csrf},
				type:"DELETE",
				url:"/members/" + Id,
				success:function (data) {
					window.location.reload();
				}
			});
		}
	});

	$('.edit-member').on("click", function(e){
		e.preventDefault();

		var id = $(this).closest("td").attr("id");
		$.ajax({
			type:"GET",
			headers: { 'accept': 'application/json'},
			url:"/members/" + id,
			success:function (data) {
				$.each(data, function(key, value) {
					$('#' + key).val(data[key]);
				});
				$('#member_id').val(data.id);
				$('#username').val(data.user != null ? data.user.username : '');

				window.history.pushState('Edit Member ' + data.memberName, 'MakerTracker', '/members/' + data.id);
				loadTrainings(id);
			}
		});
	});

	$('.new-member').on("click", function(e){
		e.preventDefault();
		resetForm($('#form'));
		window.history.pushState('Edit Member', 'MakerTracker', '/members');
	});
});

function loadTrainings(memberId) {
	$.ajax({
		type:"GET",
		headers: { 'accept': 'application/json'},
		url:"/members/" + memberId + "/trainings/",
		success:function (data) {
			$('#trainingTableBody').empty();
			
			if (data != null) {
				for (var i = 0; i < data.length; i++) {
					var newRow = '<tr>'
							+ '<td style="display:none" id="training-' + data[i].id + '">' + data[i].id +'</td>'
							+ '<td><a href="/trainingtypes/'+ data[i].trainingType.id + '">' + data[i].trainingType.name + '</a></td>'
							+ '<td>' + new Date(data[i].trainingDate).toLocaleString() + '</td></tr>';
					
					$('#trainingTableBody').append(newRow);
				}
			}
		}
	});
}