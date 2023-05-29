//错误提示
function showError(id,msg) {
    $("#"+id+"Ok").hide();
    $("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
    $("#"+id+"Err").show();
    $("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
    $("#"+id+"Err").hide();
    $("#"+id+"Err").html("");
    $("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
    $("#"+id+"Err").hide();
    $("#"+id+"Err").html("");
    $("#"+id+"Ok").show();
    $("#"+id).removeClass("input-red");
}



var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});





//页面加载完毕后
$(function () {
    //电话号码输入框失去焦点事件
    $("#phone").on("blur",function () {
        //拿到文本框内容(去除空格)
        var phone = $.trim($("#phone").val());
        if (phone == "") {
            showError("phone","手机号不能为空")
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            showError("phone","请输入正确的手机号")
        } else {
            showSuccess("phone")
        }
    })
    // 密码输入框失去焦点事件
    $("#loginPassword").on("blur",function () {
        //拿到文本框内容(去除空格)
        var loginPassword = $.trim($("#loginPassword").val())
        if(loginPassword == "") {
            showError("loginPassword", "密码不能为空")
        }
        else {
            showSuccess("loginPassword")
        }
    })

    // 验证码输入框失去焦点事件
    $("#messageCode").on("blur", function () {
        var messageCode = $.trim($("#messageCode").val());
        if (messageCode == "") {
            showError("messageCode","验证码不能为空")
        }
        else {
            showSuccess("messageCode")
        }
    });
    //获取短信验证码
    //三个层次
    //1. 第一步，当点击按钮后，先实现倒计时效果
    //2. 第二步，当点击按钮后 ，对手机号和密码进行验证
    //3. 第三步，当点击按钮后，后台发送短信后才开始倒计时
    $("#messageCodeBtn").on("click",function () {
        //隐藏验证码不能为空的提示
        hideError("messageCode")
        //先对手机号和密码的输入框内容进行验证
        $("#phone").blur();
        $("#loginPassword").blur();

        var errorText = $("div[id$='Err']").text();
        if(errorText == ""){
            //验证通过
            //① 请求后台发送短信验证码
            var phone = $.trim($("#phone").val())
            $.ajax({
                url:"/p2p/user/messageCode",
                type:"get",
                data:{
                    //注意此处数据只是为了发送短信验证码不是为了注册，所以数据只有电话号码足矣不需要密码数据
                    "phone" : phone
                },
                success:function (data) {
                    if(data.code == 1){
                        alert("验证码是："+data.messageCode)
                        //短信发送成功，开始倒计时
                        //② 开始倒计时
                        //避免重复多次开始倒计时：只有没有变灰时即蓝色时才能开始倒计时
                        if( !$("#messageCodeBtn").hasClass("on")){
                            $.leftTime(30,function(d){
                                //d.status,值true||false,倒计时是否结束;
                                //d.s,倒计时秒;
                                if(d.status){
                                    $("#messageCodeBtn").text(d.s == '00' ? '60秒后获取' : d.s+'秒后获取');
                                    //变灰
                                    $("#messageCodeBtn").addClass("on")
                                }
                                else{
                                    //恢复
                                    $("#messageCodeBtn").text("获取验证码");
                                    $("#messageCodeBtn").removeClass("on")
                                }
                            });
                        }
                    }
                    else {
                        showError("messageCode",data.message)
                    }
                },
                error:function () {
                    showError("messageCode","系统异常")
                }
            })

        }

    });

})
//登录功能
function Login() {
    $("#phone").blur();
    $("#loginPassword").blur();
    $("#messageCode").blur();

    var errorText = $("div[id$='Err']").text();
    if (errorText == "") {
        // 没有错误信息，通过了验证, 请求后台
        var phone = $.trim($("#phone").val());
        var loginPassword = $.trim($("#loginPassword").val());
        var messageCode = $.trim($("#messageCode").val());
        $.ajax({
            url : "/p2p/loan/login",
            type : "get",
            data: {
                "phone" : phone,
                "loginPassword" : $.md5(loginPassword),
                "messageCode" : messageCode
            },
            success:function (data) {

                if (data.code == 1) {
                    // 登录成功
                    window.location.href = "/p2p/index"
                } else {
                    // 登录失败
                    showError("messageCode", data.message)
                }
            },
            error:function () {
                showError("messageCode","系统繁忙，登录失败");
            }
        })

    }
}
