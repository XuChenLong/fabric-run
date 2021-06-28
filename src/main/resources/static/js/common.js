function serializeToJson(form){
		var result={};
		//获取表单输入的内容
		//{name:email,value:用户输入的内容}
		var f=form.serializeArray();
		f.forEach(function(item){
			//result.email
			result[item.name]=item.value;
			alert(result[item.name]);
		});
		alert('what??');
		return result;
		
	}