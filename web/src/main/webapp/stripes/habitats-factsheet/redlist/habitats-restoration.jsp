<%@page contentType="text/html;charset=UTF-8" %>
<%@ include file="/stripes/common/taglibs.jsp" %>
<stripes:layout-definition>
<div>
    <div style="padding: 10px">
        <div>${actionBean.redlist.capacityRecover}</div>
    </div>
    <div style="align-content: center; width: 100%; padding: 10px">
        <div class="text-center" style="align-content: center; width: 50%; padding: 6px; float: left">
            <table class="trend-table">
                <tr>
                    <td colspan="2"><h3>Trends in extent</h3></td>
                </tr>
                <tr>
                    <td colspan="2"><h4>Average current trend in quantity</h4></td>
                </tr>
                <tr>
                    <td class="trend-${actionBean.redlist.EU28TrendQuantity}">${actionBean.redlist.EU28TrendQuantity} <img class="arrow" src="images/arrow-${actionBean.redlist.EU28TrendQuantity}.png"/></td>
                    <td class="trend-${actionBean.redlist.EU28PlusTrendQuantity}">${actionBean.redlist.EU28PlusTrendQuantity} <img class="arrow" src="images/arrow-${actionBean.redlist.EU28PlusTrendQuantity}.png"/></td>
                </tr>
                <tr>
                    <td class="bold">EU28</td>
                    <td class="bold">EU28+</td>
                </tr>
            </table>
        </div>

        <div class="text-center" style="width: 50%; float: left; padding: 6px; text-align: center">
            <table class="trend-table">
                <tr>
                    <td colspan="2"><h3>Trends in quality</h3></td>
                </tr>
                <tr>
                    <td colspan="2"><h4>Average current trend in quality</h4></td>
                </tr>
                <tr>
                    <td class="trend-${actionBean.redlist.EU28TrendQuality}">${actionBean.redlist.EU28TrendQuality} <img class="arrow" src="images/arrow-${actionBean.redlist.EU28TrendQuality}.png"/></td>
                    <td class="trend-${actionBean.redlist.EU28PlusTrendQuality}">${actionBean.redlist.EU28PlusTrendQuality} <img class="arrow" src="images/arrow-${actionBean.redlist.EU28PlusTrendQuality}.png"/></td>
                </tr>
                <tr>
                    <td class="bold">EU28</td>
                    <td class="bold">EU28+</td>
                </tr>
            </table>
        </div>


</div>

</stripes:layout-definition>