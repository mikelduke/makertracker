$(document).ready(function () {
	
	var id = parseInt($('#id').attr('value'));
	if (!isNaN(id)) {
		loadTraining(id);
	}
	
	$('#memberId').on("click", function(e) {
		e.preventDefault();

		var memberId = $('#memberId').val();
		
		if (memberId != '' && !$('#member-row-' + memberId).length) {
			var memberName = $("#memberId>option:selected").html();
			addMemberRow(memberId, memberName, '', '');
		}
	});
	
	$('#teacherMemberId').on("click", function(e) {
		e.preventDefault();

		var memberId = $('#teacherMemberId').val();
		if (memberId != '' && !$('#teacher-row-' + memberId).length) {
			var memberName = $("#teacherMemberId>option:selected").html();
			addTeacherRow(memberId, memberName, '', '');
		}
	});
	
	$('#trainedMembersAdminForm').on("click", ".remove-member", function(e){
		e.preventDefault();
		
		var id = $(this).closest("td").attr("id");
		$('#member-row-' + id).remove();
	});
	
	$('#teachersForm').on("click", ".remove-teacher", function(e){
		e.preventDefault();
		
		var id = $(this).closest("td").attr("id");
		$('#teacher-row-' + id).remove();
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
						if (memberId.length > 0) {
							trainedMembers.push(memberId);
						}
					});
					
					var teachers = new Array();
					
					$('#teacherTable tbody tr').each(function(){
						var memberId = $(this)[0].id.substring("teacher-row-".length);
						if (memberId.length > 0) {
							teachers.push(memberId);
						}
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
							console.log("Members saved for training");
						}
					});
					
					$.ajax({
						headers: { 'X-CSRF-TOKEN': csrf},
						type: "POST",
						contentType: "application/json",
						url: "/trainingtypes/" + data + "/teachers/",
						data: JSON.stringify(teachers),
						dataType: 'json',
						timeout: 6000,
						success: function (dataMember) {
							if (confirm("Training Type with Id " + data + " Saved")){
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

	$('.new-training').on("click", function(e){
		e.preventDefault();
		resetForm($('#form'));
		
		if ($('#trainedMembersAdminForm').length) {
			$('#memberTableBody').empty();
		}
		
		window.history.pushState('Edit Trainings', 'MakerTracker', '/trainingtypes');
	});
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
		url:"/trainingtypes/" + id + "/assets/",
		success:function (data) {
			loadAssets(data);
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
	
	$.ajax({
		type:"GET",
		headers: { 'accept': 'application/json'},
		url:"/trainingtypes/" + id + "/teachers/",
		success:function (data) {
			loadTeachers(data);
		}
	});
}

function loadMembers(members) {
	if ($('#trainedMembersAdminForm').length) {
		$('#memberTableBody').empty();
		
		if (members != null) {
			for (var i = 0; i < members.length; i++) {
				addMemberRow(members[i].member.id, members[i].member.memberName, members[i].trainingDate, members[i].addedBy.username);
			}
		}
	} else {
		$('#memberTableBody').empty();
		
		if (members != null) {
			for (var i = 0; i < members.length; i++) {
				var newRow = '<tr><td id="member-' + members[i].member.memberName + '">' + members[i].member.memberName + '</td></tr>';
				
				$('#memberNameTable').find('tbody').append(newRow);
			}
		}
	}
}

function addMemberRow(memberId, memberName, trainingDate, addedBy) {
	var date = '';
	
	if (trainingDate != '' && trainingDate != null) {
		date = new Date(trainingDate).toLocaleString();
	}
	
	var newRow = '<tr id="member-row-' + memberId + '" class="member-row">';
	newRow += '<td id="member-id-' + memberId + '" style="display:none">' + memberId + '</td>';
	newRow += '<td id="member-' + memberName + '">' + memberName + '</td>';
	newRow += '<td>' + date + '</td>';
	newRow += '<td>' + addedBy + '</td>';
	newRow += '<td id="' + memberId + '"><button class="btn btn-danger remove-member">X</button></td>';
	newRow += '</tr>';
	
	$('#memberTable').find('tbody').append(newRow);
}

function loadTeachers(teacher) {
	if ($('#teachersForm').length) {
		$('#teacherTableBody').empty();
		
		if (teacher != null) {
			for (var i = 0; i < teacher.length; i++) {
				addTeacherRow(teacher[i].member.id, teacher[i].member.memberName, teacher[i].addedDate, teacher[i].addedBy.username);
			}
		}
	} else {
		$('#teacherTableBody').empty();
		
		if (teacher != null) {
			for (var i = 0; i < teacher.length; i++) {
				var newRow = '<tr><td id="teacher-' + teacher[i].member.memberName + '">' + teacher[i].member.memberName + '</td></tr>';
				
				$('#teacherNameTable').find('tbody').append(newRow);
			}
		}
	}
}

function addTeacherRow(memberId, memberName, trainingDate, addedBy) {
	var date = '';
	
	if (trainingDate != '' && trainingDate != null) {
		date = new Date(trainingDate).toLocaleString();
	}
	
	var newRow = '<tr id="teacher-row-' + memberId + '" class="teacher-row">';
	newRow += '<td id="teacher-id-' + memberId + '" style="display:none">' + memberId + '</td>';
	newRow += '<td id="teacher-' + memberName + '">' + memberName + '</td>';
	newRow += '<td>' + date + '</td>';
	newRow += '<td>' + addedBy + '</td>';
	newRow += '<td id="' + memberId + '"><button class="btn btn-danger remove-teacher">X</button></td>';
	newRow += '</tr>';
	
	$('#teacherTable').find('tbody').append(newRow);
}

function loadAssets(assets) {
	$('#assetsTableBody').empty();
	
	if (assets != null) {
		for (var i = 0; i < assets.length; i++) {
			var newRow = '<tr>' + 
					'<td id="assets-' + assets[i].tenbitId + '">' + assets[i].tenbitId + '</td>' +
					'<td>' + assets[i].title + '</td>' +
					'</tr>';
//			$('#assets').find('tbody').append(newRow);
			$('#assetsTableBody').append(newRow);
		}
	}
}