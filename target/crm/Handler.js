/**
 * Created by palsulea on 8/31/2016.
 */
function fileRead() {

    var formData = new FormData(document.forms[0]);
    $.ajax({
        url:"/insertDataInDB.do",
        type:"post",
        data:formData,
        processData:false,
        contentType:false,
        error:function (result) {
            console.log(result)
        },
        success:function (result) {
            console.log("Entered Into Database")
            console.log(result);
        }
    })
}