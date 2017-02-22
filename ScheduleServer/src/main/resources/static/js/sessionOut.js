$.ajaxSetup({
    contentType:"application/x-www-form-urlencoded;charset=utf-8",
    complete:function(XMLHttpRequest,textStatus){
        var sessionStatus=XMLHttpRequest.getResponseHeader("sessionStatus"); //通过XMLHttpRequest取得响应头，sessionstatus，
        if(sessionStatus=="timeout"){
            //session超时就处理 ，指定要跳转的页面
            window.location.href="/admin/loginPage";
        }
    }
});