<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="section" value="${assignment.section}" />

<c:if test="${not assignment.published}">
<script>
$(function(){
    $("input[name='numOfChoices']").change(function(){
        var newNumOfChoices = $(this).val();
        var oldNumOfChoices = $("#choicesInput").children("input[type='text']").length;
        if( newNumOfChoices > oldNumOfChoices )
        {
            for( var i = oldNumOfChoices ; i < newNumOfChoices ; ++i )
                $("#choicesInput").append( "<span>Choice #" + (i+1) + ":</span> " +
                    "<input id='choices" + i + "' name='choices[" + i + "]' type='text' class='leftinput' style='width: 70%;' /> " + 
                    "<input id='correctSelections" + (i+1) + "' name='correctSelections' type='checkbox' value='" + i + "' />" +
                    "<input type='hidden' name='_correctSelections' value='off' /> <br /><br />" );
            if( $("input[name='maxSelections']").val() == oldNumOfChoices )
                $("input[name='maxSelections']").val( newNumOfChoices );
        }
        if( newNumOfChoices < oldNumOfChoices )
        {
            $("#choicesInput span:gt(" + (newNumOfChoices-1) +")").remove();
            $("#choicesInput input[type='text']:gt(" + (newNumOfChoices-1) +")").remove();
            $("#choicesInput input[type='checkbox']:gt(" + (newNumOfChoices-1) +")").remove();
            $("#choicesInput input[type='hidden']:gt(" + (newNumOfChoices-1) +")").remove();
            $("#choicesInput br:gt(" + (newNumOfChoices*2-1) +")").remove();
            if( $("input[name='maxSelections']").val() > newNumOfChoices )
                $("input[name='maxSelections']").val( newNumOfChoices );
        }
    });
});
function deleteQuestion()
{
    var msg = "Are you sure you want to delete this question?";
    if( confirm(msg) )
        window.location.href = "deleteQuestion?assignmentId=${assignment.id}&sectionIndex=${param.sectionIndex}&questionId=${question.id}";
}
</script>
</c:if>

<ul id="title">
<li><a class="bc" href="<c:url value='/section/taught' />">${section.quarter}</a></li>
<li><a class="bc" href="<c:url value='/section/taught#section-${section.id}' />">${section.course.code} - ${section.number}</a></li>
<li><a class="bc" href="edit?id=${assignment.id}&sectionIndex=${param.sectionIndex}">${assignment.name}</a></li>
<li>Edit Question</li>
<c:if test="${not assignment.published}">
<li class="align_right"><a href="javascript:deleteQuestion()"><img title="Delete Question"
  alt="[Delete Question]" src="<c:url value='/img/icons/page_delete.png' />" /></a></li>
</c:if>
</ul>

<form:form modelAttribute="question">
<h4>Question description:</h4>
<form:textarea path="description" cssStyle="width: 80%; height: 8em;" />
<div class="error"><form:errors path="description" /></div>

<c:choose>

<c:when test="${question.type == 'CHOICE'}">
<h4>
<c:if test="${not assignment.published}">
Number of choices: <form:input path="numOfChoices" cssStyle="width: 2em;" />
</c:if>
</h4>
<div id="choicesInput">
<c:forEach items="${question.choices}" var="choice" varStatus="choiceStatus">
<span>Choice #${choiceStatus.index+1}:</span>
  <form:input path="choices[${choiceStatus.index}]" cssClass="leftinput" cssStyle="width: 70%;" />
  <form:checkbox path="correctSelections" value="${choiceStatus.index}" /> <br /><br />
</c:forEach>
</div>
<h4>Number of selections:
<form:input path="minSelections" cssClass="forminput" cssStyle="width: 1em;" /> -&gt;
<form:input path="maxSelections" cssClass="forminput" cssStyle="width: 1em;" /></h4>
</c:when>

<c:when test="${question.type == 'RATING'}">
<h4>Rating:
<form:input path="minRating" cssClass="forminput" cssStyle="width: 2em;" /> -&gt;
<form:input path="maxRating" cssClass="forminput" cssStyle="width: 2em;" /></h4>
</c:when>

<c:when test="${question.type == 'TEXT'}">
<h4>Text Length:
<form:input path="textLength" cssClass="forminput" cssStyle="width: 4em;" />
</h4>
</c:when>

</c:choose>

<h4>Point Value:
<form:input path="pointValue" cssClass="forminput" cssStyle="width: 2em;" />
</h4>

<input type="hidden" name="assignmentId" value="${assignment.id}" />
<input type="hidden" name="sectionIndex" value="${param.sectionIndex}" />
<input class="subbutton" type="submit" value="Save" />
</form:form>

<script type="text/javascript">
  CKEDITOR.replaceAll();
</script>
