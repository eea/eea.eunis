<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/stripes/common/taglibs.jsp"%>
<%
    String btrail = "eea#" + application.getInitParameter( "EEA_HOME" ) + ",home#index.jsp,habitat_types";
%>
<stripes:layout-render name="/stripes/common/template.jsp" pageTitle="Error" bookmarkPageName="habitats" btrail="<%= btrail %>">
    <stripes:layout-component name="head">

    </stripes:layout-component>
    <stripes:layout-component name="contents">

        <div class="error-msg">
                ${actionBean.message}
        </div>

    </stripes:layout-component>
</stripes:layout-render>
