/**
 * 
 */


function imageChoose(obj){
	let f = obj.files[0];
	if(!f.type.match("image.*")){
		alert("이미지를 등록해야 됨");
		return;
	}
	let reader = new FileReader();
	reader.onload = (e) =>{
		$("#imageUploadPreview").attr('src',e.target.result);
	}
	reader.readAsDataURL(f)
	
}