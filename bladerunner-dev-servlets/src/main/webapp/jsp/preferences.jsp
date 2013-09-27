<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/javascript;charset=ISO-8859-1" %>
<%@ page isELIgnored = "false" %> <%-- remove when 2.3 DTD removed form web.xml --%>
{<c:forEach var="pref" items="${PREFERENCES}" varStatus="status">
	"${pref.name}":"${pref.json}"<c:if test="${status.last != true}">, </c:if>
</c:forEach>}