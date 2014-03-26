<%@ include file="include.jsp"%>


<link href="<c:url value="/resources/css/default.css" />"
	rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="<c:url value="/resources/css/validationEngine.jquery.css"/>" type="text/css"/>
<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.7.2.min.js"/>"></script>
<script src="<c:url value="/resources/js/jquery.validationEngine-en.js"/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value="/resources/js/jquery.validationEngine.js"/>" type="text/javascript" charset="utf-8"></script>
	
<script>
$(document).ready(function(){
    $("#page").validationEngine();
   });
   </script>
<form name="forgot" method="post" id="page"  action="forgotPasswordSubmit" >
	<br>
	<h1>
	
	
		&nbsp;&nbsp;&nbsp;<fmt:message key="title.forgotpassword" />
	</h1>
	<hr>
	<font color="red"> <c:if test="${error ne null}"><fmt:message key="error.${error}"/></c:if> </font>
	<font color="green"><c:out value="${message}"/></font>
		
		<fieldset id="inputs">
			
			<table class="center">
			<tr><br></tr>
			<tr><br></tr>
			<tr><td><fmt:message key="home.username" /></td><td>
			<spring:bind path="forgot.userName">
			<input type="text"  class="right curved borderL validate[required]" name="<c:out value="${status.expression}"/>" placeholder="<fmt:message key="home.username" />"
							value="<c:out value="${status.value}"/>" >
							</spring:bind>
			</td></tr>
			<tr><td><fmt:message key="label.message.email" /></td><td>
			<spring:bind path="forgot.emailId">
			<input type="text" class="right curved borderL validate[required]" name="<c:out value="${status.expression}"/>" placeholder="<fmt:message key="label.message.email" />"
							value="<c:out value="${status.value}"/>" >
							</spring:bind>
			</td></tr>
				</table>		
		<table class="center">
		<tr>
		<td><input type="submit" class="center curved borderL" id="submit"/></td>
		<td>&nbsp;</td>
		<td><input type="reset" class="center curved borderL" id="Reset"/></td>
		</tr>
		</table>
		
		
		
		
		
		
		
		
	</form>
	
	
	