
/**
 * 控制导航栏中哪个menu高亮显示
 */
function activityNav(index) {
  $("ul.nav li").each(function(n, child) {
    $(child).removeClass("active");
    $(child).addClass("dropdown");
  });
  
  $("ul.nav li:eq(" + index + ")").addClass("active");
}

/**
 * 返回下拉滚动条滚动了的距离
 * 
 * @returns {int}
 */
function getCcrollTop() {
	return window.pageYOffset ||
    document.documentElement.scrollTop || 
    document.body.scrollTop || 0;
}

/**
 * 判断下拉滚动条是否到达了低端
 * 
 * @returns {Boolean}
 */
function lowEnough() {
    var pageHeight = Math.max(document.body.scrollHeight,
      document.body.offsetHeight);
    var viewportHeight = window.innerHeight ||
      document.documentElement.clientHeight || 
      document.body.clientHeight || 0;
    // Trigger for scrolls within 20 pixels from page bottom
    return pageHeight - viewportHeight - getCcrollTop() < 20;
}

/**
 * 操作成功
 * @param msg
 */
function operateSuccess(msg) {
	$(".alert strong").text(msg);
    $(".alert").removeClass("alert-error").addClass("alert-success").show("slow");
    setTimeout(function(){$(".alert").hide("slow");}, 2000);
}

/**
 * 操作失败
 * @param msg
 */
function operateFailure(msg) {
	$(".alert strong").text(msg);
    $(".alert").removeClass("alert-success").addClass("alert-error").show("slow");
    setTimeout(function(){$(".alert").hide("slow");}, 2000);	
}
