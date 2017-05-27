/**
 * 
 */
$(function(){
	getData();
function getData(){
		$.ajax({
			url:'user/showUser.shtml',
		    type:'POST', //GET
		    async:true,    //或false,是否异步
		    dataType:"json",
		    data:{
		        id:1
		    },
		    success:function(data,textStatus,jqXHR){
		        $("#userName").val(data.userName);
		        $("#password").val(data.password);
		        $("#age").val(data.age);
		    },
		    error:function(xhr,textStatus){
		        console.log("网络连接错误");
		    }
});

}
}); 