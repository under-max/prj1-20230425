let checkEmail = true;
let checkNickName = true;
let checkPassword = true; 

const enableSubmit = () => {
	if(checkEmail && checkNickName && checkPassword){
		$("#signupSubmit").removeAttr("disabled");
	} else {
		$("#signupSubmit").attr("disabled", "");
	}
}

$("#inputNickName").keyup(()=>{
	checkNickName = false;
	enableSubmit();
});

$("#inputEmail").keyup(()=>{
	checkEmail = false;
	enableSubmit();
});

//nickName중복 확인
$("#checkNickNameBtn").click(()=>{
	const nickName = $("#inputNickName").val();
	
	//member정보가 비어있으면 true 있으면 false
	// 들어있다면 중복된nickName이 있다는 이야기 
	$.ajax("/member/checkNickName/" + nickName, {
		
		success: (data)=>{
			if(data.available || data.originName == nickName){							
				$("#availableId").removeClass("d-none");
				$("#notAvailableId").addClass("d-none"); //중복된 별명
				checkNickName = true;
			} else{
							
				$("#availableId").addClass("d-none");
				$("#notAvailableId").removeClass("d-none"); //중복된 별명
				checkNickName = false;
			}
		},//수정버튼 활성화 비활성화
		complete: enableSubmit
	})
})


//email중복 확인
$("#checkEmailBtn").click(()=>{
	const email = $("#inputEmail").val();
	
	//member정보가 비어있으면 true 있으면 false
	// 들어있다면 중복된nickName이 있다는 이야기 
	$.ajax("/member/checkEmail/" + email, {
		
		success: (data)=>{
			if(data.available || data.originEmail === email){
				console.log(data.originEmail);					
				$("#availableEmail").removeClass("d-none");
				$("#notAvailableEmail").addClass("d-none"); //중복된 별명
				checkEmail = true;
			} else {								
				$("#availableEmail").addClass("d-none");
				$("#notAvailableEmail").removeClass("d-none"); //중복된 별명
				checkEmail = false;
			}
		},
		complete: enableSubmit		
	});
});


// 패스워드, 패스워드체크 인풋에 키업 이벤트 발생하면
$("#inputPassword, #inputPasswordCheck").keyup(function() {
	// 패스워드에 입력한 값
	const pw1 = $("#inputPassword").val();
	// 패스워드확인에 입력한 값이
	const pw2 = $("#inputPasswordCheck").val();
	console.log(pw1);
	console.log(pw2);

	if (pw1 === pw2) {
		// 같으면
		// submit 버튼 활성화
		$("#signupSubmit").removeClass("disabled");
		// 패스워드가 같다는 메세지 출력
		$("#passwordSuccessText").removeClass("d-none");
		$("#passwordFailText").addClass("d-none");
		checkPassword = true;
	} else {
		// 그렇지 않으면
		// submit 버튼 비활성화
		$("#signupSubmit").addClass("disabled");
		// 패스워드가 다르다는 메세지 출력
		$("#passwordFailText").removeClass("d-none");
		$("#passwordSuccessText").addClass("d-none");
		checkPassword = false;
	}
	enableSubmit();
})	
