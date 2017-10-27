$(document).ready(function () {
	
	$('#btn_addmember').on("click", function(e) {
		e.preventDefault();

		var memberId = $('#memberId').val();
		
		if (memberId != '' && !$('#member-row-' + memberId).length) {
			var memberName = $("#memberId>option:selected").html()
			addMemberRow(memberId, memberName);
		}
	});
	
	function addMemberRow(memberId, memberName) {
		var newRow = '<tr id="member-row-' + memberId + '" class="member-row">';
		newRow += '<td id="member-id-' + memberId + '" style="display:none">' + memberId + '</td>';
		newRow += '<td id="member-' + memberName + '">' + memberName + '</td>';
		newRow += '<td id="' + memberId + '"><button class="btn btn-danger remove-member">X</button></td>';
		newRow += '</tr>';
		
		$('#memberTable').find('tbody').append(newRow);
	}
	
	$('#trainedMembersAdminForm').on("click", ".remove-member", function(e){
		e.preventDefault();
		
		var id = $(this).closest("td").attr("id");
		$('#member-row-' + id).remove();
	});
	
	$('#btn_submit').on("click",function (e) {
		e.preventDefault();
		
		var formAr = formToObject($('#form'));
		var csrf = $("[name='_csrf']").val();
		
		formAr["id"] = $('#trainingTypeId').val();
		
//		var trainedMembers = new Array();
//		$('#memberTable tbody tr').each(function(){
//			trainedMembers.push($(this)[0].id.substring("member-row-".length));
//		});
		
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
					$('#memberTable tbody tr').each(function(){
						var memberId = $(this)[0].id.substring("member-row-".length);
						
						$.ajax({
							headers: { 'X-CSRF-TOKEN': csrf},
							type: "POST",
							contentType: "application/json",
							url: "/trainings/" + data + "/members/" + memberId,
							data: '',//JSON.stringify(formAr),
							dataType: 'json',
							timeout: 6000,
							success: function (data) {}
						});
					});
					
					if (confirm("Training Type with Id " +data + " Saved")){
						window.location.reload();
					}
				}
			});
		}
	});

//	//Complete order button handler
//	$('.delete-asset').on("click", function(e){
//		e.preventDefault();
//		if(confirm("Delete?")){
//			var id = $(this).closest("td").attr("id");
//			var csrf = $("[name='_csrf']").val();
//			$.ajax({
//				headers: { 'X-CSRF-TOKEN': csrf},
//				type:"DELETE",
//				url:"/assets/" + id,
//				success:function (data) {
//					window.location.reload();
//				}
//			});
//		}
//	});

//	$('.edit-asset').on("click", function(e){
//		e.preventDefault();
//
//		var id = parseInt($(this).closest("td").attr("id"));
//		$.ajax({
//			type:"GET",
//			headers: { 'accept': 'application/json'},
//			url:"/assets/" + id,
//			success:function (data) {
//				$.each(data, function(key, value) {
//					$('#' + key).val(data[key]);
//				});
//				$('#assetId').val(data.id)
//				$('#trainingRequired').prop('checked', data.trainingRequired);
//				
//				if ($('#trainedMembersAdminForm').length) {
//					$('#memberTableBody').empty();
//					
//					if (data.trainingRequired) {
//						$('#trainedMembersAdminForm').show();
//					} else {
//						$('#trainedMembersAdminForm').hide();
//					}
//					
//					var members = data.members;
//					if (members != null) {
//						for (var i = 0; i < members.length; i++) {
//							addMemberRow(members[i].id, members[i].memberName)
//						}
//					}
//				} else if ($('#trainedMembersForm').length) {
//					if (data.trainingRequired) {
//						$('#trainedMembersForm').show();
//						
//						var members = data.memberNames;
//						if (members != null) {
//							for (var i = 0; i < members.length; i++) {
//								var newRow = '<tr><td id="member-' + members[i] + '">' + members[i] + '</td></tr>';
//								
//								$('#memberNameTable').find('tbody').append(newRow);
//							}
//						}
//					} else {
//						$('#trainedMembersForm').hide();
//					}
//				}
//
//				window.history.pushState('Edit Asset ' + data.id, 'MakerTracker', '/assets/' + data.id);
//			}
//		});
//	});

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
//		window.history.pushState('Edit Assets', 'MakerTracker', '/assets');
//	});
});