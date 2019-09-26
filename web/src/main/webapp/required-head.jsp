<%@page contentType="text/html;charset=UTF-8"%>
<%@ page import="eionet.eunis.stripes.actions.UpdateTemplateActionBean" %>
<jsp:useBean id="SessionManager" class="ro.finsiel.eunis.session.SessionManager" scope="session" />
<%= SessionManager.getWebContent().getText(UpdateTemplateActionBean.REQUIRED_HEAD)%>
