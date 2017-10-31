$(document).ready(function () {
	
	var id = parseInt($('#id').attr('value'));
	if (!isNaN(id)) {
		loadTraining(id);
	}
	
	$('#btn_addmember').on("click", function(e) {
		e.preventDefault();

		var memberId = $('#memberId').val();
		
		if (memberId != '' && !$('#member-row-' + memberId).length) {
			var memberName = $("#memberId>option:selected").html()
			addMemberRow(memberId, memberName);
		}
	});
	
	$('#trainedMembersAdminForm').on("click", ".remove-member", function(e){
		e.preventDefault();
		
		var id = $(this).closest("td").attr("id");
		$('#member-row-' + id).remove();
	});
	
	$('#btn_submit').on("click",function (e) {
		e.preventDefault();
		
		var formAr = formToObject($('#form'));
		var csrf = $("[name='_csrf']").val();
		
		formAr["id"] = $('#id').val();
		
		if($.trim($('#name')) === ""){
			alert("Name cannot be empty");
		} else {
			$.ajax({
				headers: { 'X-CSRF-TOKEN': csrf},
				type: "POST",
				contentType: "application/json",
				url: "/trainingtypes",
				data: JSON.stringify(formAr),
				dataType: 'json',
				timeout: 6000,
				success: function (data) {
					var trainedMembers = new Array();
					
					$('#memberTable tbody tr').each(function(){
						var memberId = $(this)[0].id.substring("member-row-".length);
						trainedMembers.push(memberId);
					});
					
					$.ajax({
						headers: { 'X-CSRF-TOKEN': csrf},
						type: "POST",
						contentType: "application/json",
						url: "/trainings/" + data + "/members/",
						data: JSON.stringify(trainedMembers),
						dataType: 'json',
						timeout: 6000,
						success: function (dataMember) {
							if (confirm("Training Type with Id " +data + " Saved")){
								window.location.reload();
							}
						}
					});
				}
			});
		}
	});

	$('.delete-training').on("click", function(e){
		e.preventDefault();
		if(confirm("Delete?")){
			var id = $(this).closest("td").attr("id");
			var csrf = $("[name='_csrf']").val();
			$.ajax({
				headers: { 'X-CSRF-TOKEN': csrf},
				type:"DELETE",
				url:"/trainingtypes/" + id,
				success:function (data) {
					window.history.pushState('Edit Training', 'MakerTracker', '/trainingtypes/');
					window.location.reload();
				}
			});
		}
	});

	$('.edit-training').on("click", function(e){
		e.preventDefault();

		var id = parseInt($(this).closest("td").attr("id"));
		loadTraining(id);
	});

//	$('.new').on("click", function(e){
//		e.preventDefault();
//		resetForm($('#form'));
//		
//		$('#dateAcquired').val('');
//		$('#webLink').val('');
//		$('#dateRemoved').val('');
//		$('#trainingRequired').prop('checked', false);
//		$('#accessControlTimeMS').val('');
//		
//		if ($('#trainedMembersAdminForm').length) {
//			$('#memberTableBody').empty();
//			$('#trainedMembersAdminForm').hide();
//		}
//		$('#accessControlTimeMSForm').hide();
//		
//		window.history.pushState('Edit Trainings', 'MakerTracker', '/trainingtypes');
//	});
});

function loadTraining(id) {
	$.ajax({
		type:"GET",
		headers: { 'accept': 'application/json'},
		url:"/trainingtypes/" + id,
		success:function (data) {
			$.each(data, function(key, value) {
				$('#' + key).val(data[key]);
			});
			
			window.history.pushState('Edit Training ' + data.id, 'MakerTracker', '/trainingtypes/' + data.id);
		}
	});
	
	$.ajax({
		type:"GET",
		headers: { 'accept': 'application/json'},
		url:"/trainings/" + id + "/members/",
		success:function (data) {
			loadMembers(data);
		}
	});
}

function loadMembers(members) {
	if ($('#trainedMembersAdminForm').length) {
		$('#memberTableBody').empty();
		
		if (members != null) {
			for (var i = 0; i < members.length; i++) {
				addMemberRow(members[i].id, members[i].memberName)
			}
		}
	} else if ($('#trainedMembersForm').length) {
		$('#trainedMembersForm').show();
		
		var members = data.memberNames;
		if (members != null) {
			for (var i = 0; i < members.length; i++) {
				var newRow = '<tr><td id="member-' + members[i] + '">' + members[i] + '</td></tr>';
				
				$('#memberNameTable').find('tbody').append(newRow);
			}
		}
	}
}

function addMemberRow(memberId, memberName) {
	var newRow = '<tr id="member-row-' + memberId + '" class="member-row">';
	newRow += '<td id="member-id-' + memberId + '" style="display:none">' + memberId + '</td>';
	newRow += '<td id="member-' + memberName + '">' + memberName + '</td>';
	newRow += '<td id="' + memberId + '"><button class="btn btn-danger remove-member">X</button></td>';
	newRow += '</tr>';
	
	$('#memberTable').find('tbody').append(newRow);
}