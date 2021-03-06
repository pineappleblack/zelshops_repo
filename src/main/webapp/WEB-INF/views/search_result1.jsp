<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>  
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<spring:url value="/resources/style.css" var="styleCSS"/>
<spring:url value="/resources/zelshop.png" var="zelshopPNG"/>
<link href="<c:url value="/resources/css/style.css"/>" rel="stylesheet">


<title>ZELshops.ru - только лучшие магазины Зеленограда</title>
</head>
<body>
	<c:if test="${empty currentUser}">
	<h3><a href="login">Вход</a></h3>
	</c:if>
	<c:if test="${not empty currentUser}">
	  <table>
			<tr>
				<td>Пользователь:</td>
				<td>${currentUser.username}</td>
				<td>Роль:</td>
				<td>${currentAccess}</td>
			</tr>
			<tr>
				<td><h3><a href="logout">Выход</a></h3></td>
			</tr>
			<tr>
				<td><h3><a href="newshops">Неактивные магазины</a></h3></td>
			</tr>
			<tr>
				<td><h3><a href="checkshops">Магазины, требующие проверки</a></h3></td>
			</tr>
			<tr>
				<td><h3><a href="addshop">Добавить магазин</a></h3></td>
			</tr>
		  </table>
	</c:if>
	
	<img src="<c:url value='/resources/images/zelshop.png'/>" class="logo"/>
    <div align="center">
        <form:form action="search" method="post" commandName="searchForm">
            <div class="search-block">
            	<form:input path="pattern" class="search"/><br/>
            	<input type="submit" value="" class="search-icon"/>
            	<div class="sel">
            		<form:select path="spec" items="${specList}" />
            	</div>
            </div>
        </form:form>
    </div>
      		<c:set var="first" value="${isFirstVisit}"/>
      		<c:if test="${!first}">
            <h1>Результаты</h1>
            <c:if test="${empty shopList}">
            <h1>Ничего не найдено</h1>
            </c:if>
            </c:if>
            	<div class="shops">
                <c:forEach var="shop" items="${shopList}" varStatus="status">
                
                
                <div class = "shop"><b>${shop.name}</b>
                	<div class = "crop"><img src = "resources/images/5ka.jpg" class = "shop-image"></div>
                	<div class = "info">
                		<strong>Cайт:</strong><a href="http://${shop.site}">${shop.site}</a><br>
						<strong>Адрес:</strong>${shop.address}<br>
						<strong>Телефон:</strong>${shop.telephone}<br>
						<strong>Сфера:</strong>${shop.spec}<br>
						<c:if test="${not empty currentAccess}">
						<a href="updateshop?id=${shop.id}">Изменить</a>
						<a href="deleteshop?id=${shop.id}">Удалить</a>
						</c:if>
						<c:if test="${empty currentAccess}">
						<a href="checkshop?id=${shop.id}">Информация не верна</a>
						</c:if>
					</div>	
                	

                	</div>
                </c:forEach>              
           </div>
        
       
</body>
</html>