  var errMessageForm = "Please type a few letters from species scientific name.";

  function validateForm()
  {
    document.eunis.scientificName.value = trim(document.eunis.scientificName.value);
    var searchString = document.eunis.scientificName.value;
    if (searchString == "") {
        alert(errMessageForm);
      return false;
    }
    return true;
  }

  function openHelper(URL)
  {
    document.eunis.scientificName.value = trim(document.eunis.scientificName.value);
    var scientificName = document.eunis.scientificName.value;
    if(scientificName=="")
    {
      alert(errMessageForm);
    } else {
      var groupName = escape(document.eunis.groupName.value);
      var relationOp=escape(document.eunis.relationOp.value);
      var URL2= URL + '?&scientificName=' + scientificName+'&relationOp='+relationOp+'&groupName='+groupName;
      eval("page = window.open(URL2, '', 'scrollbars=yes,toolbar=0, resizable=yes, location=0,width=400,height=500,left=490,top=0');");
    }
  }
