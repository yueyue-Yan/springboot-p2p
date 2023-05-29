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


//页面加载完毕
$(function() {
    //注册协议确认
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
    //电话号码输入框失去焦点事件
    $("#phone").on("blur",function () {
        //拿到文本框内容(去除空格)
        var phone = $.trim($("#phone").val());
        if (phone == "") {
            showError("phone","手机号不能为空")
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            showError("phone","请输入正确的手机号")
        } else {
            //alert("success")
            // 查询数据库，该手机号是否可用
            $.ajax({
                url:"/p2p/loan/checkPhone",
                type:"get",
                data:"phone=" + phone,
                success:function (data) {
                    if (data.code == 1) {
                        // 手机号可用
                        showSuccess("phone")
                    } else {
                        // 手机号不可用
                        showError("phone",data.message);
                    }
                },
                error:function () {
                    showError("phone","系统繁忙")
                }
            })
        }
    })
    // 密码输入框失去焦点事件
    $("#loginPassword").on("blur",function () {
        //拿到文本框内容(去除空格)
        var loginPassword = $.trim($("#loginPassword").val())
        if(loginPassword == ""){
            showError("loginPassword","密码不能为空")
        }else if(loginPassword.length<6 || loginPassword.length>20){
            showError("loginPassword","密码长度只能在6~20位之间")
        }
        else if(!/^[0-9a-zA-Z]+$/.test(loginPassword)){
            showError("loginPassword","密码字符只可使用数字和大小写英文字母")
        }
        else if(!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
            showError("loginPassword","密码应同时包含英文和数字")
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
    })

    //注册按钮的单击事件
    $("#btnRegist").on("click",function () {
        // 触发手机号和密码框的失去焦点事件
        $("#phone").blur();
        $("#loginPassword").blur();
        $("#messageCode").blur();
        // 判断手机号和密码框是否通过验证
        // 选择器： 标签[属性 $= xxx]:匹配给定的属性是以xxx为结尾的的元素； ^=:以xxx为开头
        var errorText = $("div[id $= 'Err']").text();
        if(errorText == ""){
            //信息合格，开始注册
            //准备数据
            var phone = $.trim($("#phone").val());
            var loginPassword = $.trim($("#loginPassword").val());
            var messageCode = $.trim($("#messageCode").val());
            $("#loginPassword").val($.md5(loginPassword));
            //开始注册
            $.ajax({
                url:"/p2p/loan/register",
                type:"get",
                data:{
                    "phone" : phone,
                    "loginPassword" : $.md5(loginPassword),
                    "messageCode": messageCode
                },
                success:function (data) {
                    if (data.code == 1) {
                        // 注册成功,跳转到实名认证界面
                        window.location.href = "/p2p/loan/page/realName"
                    }
                    else if (data.code == -1) {
                        // 注册失败，显示错误
                        showError("loginPassword", data.message)
                    }
                    else if (data.code == -2) {
                        // 注册失败，验证码错误
                        showError("messageCode", data.message)
                    }
                },
                error:function () {
                    showError("loginPassword","注册失败，系统异常");
                }

            })
        }

    })
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

});
