//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

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


//页面加载完毕后
$(function() {
    //同意实名认证协议
    $("#agree").click(function(){
        var ischeck = document.getElementById("agree").checked;
        if (ischeck) {
            $("#btnRegist").attr("disabled", false);
            $("#btnRegist").removeClass("fail");
        } else {
            $("#btnRegist").attr("disabled","disabled");
            $("#btnRegist").addClass("fail");
        }
    });

    // 手机号失去焦点事件
    $("#phone").on("blur",function () {
        var phone = $.trim($("#phone").val());
        if (phone == "") {
            showError("phone","手机号不能为空")
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            showError("phone","请输入正确的手机号")
        } else {
            showSuccess("phone");
        }
    });

    // 真实姓名输入框失去焦点验证
    $("#realName").on("blur", function () {
        var realName = $.trim($("#realName").val());
        if (realName == "") {
            showError("realName","请输入姓名");
            //正则表达式中文验证： ^[\u4e00-\u9fa5]{0,}$
        } else if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)) {
            showError("realName", "请输入正确的中文姓名")
        } else {
            showSuccess("realName");
        }
    })

    // 身份证失去焦点事件
    $("#idCard").on("blur", function () {
        var idCard = $.trim($("#idCard").val())
        if (idCard == "") {
            showError("idCard", "请输入框身份证号码")
        } else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)) {
            showError("idCard", "请输入正确的身份证号码")
        } else {
            showSuccess("idCard");
        }
    });

    // 短信验证码输入框的失去焦点事件
    $("#messageCode").on("blur", function () {
        var messageCode = $.trim($("#messageCode").val())
        if (messageCode == "") {
            showError("messageCode", "验证码不能为空")
        } else if (!/^\d{6}$/.test(messageCode)) {
            showError("messageCode", "请输入正确的验证码")
        } else {
            showSuccess("messageCode");
        }
    })

    // 认证按钮的单击事件
    $("#btnRegist").on("click", function () {
        // 触发上面各个输入框的失去焦点事件
        $("#phone").blur();
        $("#realName").blur();
        $("#idCard").blur();
        $("#messageCode").blur();

        var errorText = $("div[id$='Err']").text();
        if (errorText == "") {
            // 没有错误，才开始请求后台，进行实名认证
            var phone = $.trim($("#phone").val());
            var realName = $.trim($("#realName").val());
            var idCard = $.trim($("#idCard").val());
            var messageCode = $.trim($("#messageCode").val());
            // 没有错误，请求后台，开始实名认证
            $.ajax({
                url:"/p2p/loan/realName",
                type:"get",
                data:{
                    "phone" : phone,
                    "realName" : realName,
                    "idCard" : idCard,
                    "messageCode" : messageCode
                },
                success:function (data) {
                    if (data.code == 1) {
                        // 实名认证成功,跳转到个人中心
                        window.location.href = "/p2p/loan/myCenter"
                    } else {
                        showError("messageCode","认证失败")
                    }
                },
                error:function () {
                    showError("messageCode","系统异常，认证失败")
                }
            })

        }
    })

    // 获取短信验证码
    $("#messageCodeBtn").on("click",function () {
        // 隐藏验证码提示错误
        hideError("messageCode")
        // 对手机号和密码输入框进行验证
        $("#phone").blur();
        $("#realName").blur();
        $("#idCard").blur();

        var errorText = $("div[id$='Err']").text();
        if (errorText == "") {
            var phone = $.trim($("#phone").val())
            // 请求后台发送短信验证码
            $.ajax({
                url:"/p2p/user/messageCode",
                type:"get",
                data: {
                    "phone":phone
                },
                success:function (data) {
                    if (data.code == 1) {
                        // 短信发送成功,开始倒计时
                        alert('短信验证码是:'+data.messageCode)
                        // 输入框通过了验证，没有错误
                        if (!$("#messageCodeBtn").hasClass("on")) {
                            $.leftTime(60,function(d){
                                //d.status,值true||false,倒计时是否结束;
                                if (d.status) {
                                    //d.s,倒计时秒;
                                    $("#messageCodeBtn").text(d.s == '00' ? '60秒后获取' : d.s+'秒后获取');
                                    $("#messageCodeBtn").addClass("on")
                                } else {
                                    $("#messageCodeBtn").text("获取验证码");
                                    $("#messageCodeBtn").removeClass("on")
                                }

                            });
                        }
                    } else {
                        showError("messageCode",data.message)
                    }
                },
                error:function () {
                    showError("messageCode","系统异常")
                }
            })



        }
    });


});
