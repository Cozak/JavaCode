var APMPrototypeAgent = function(){

	//for the navigation timing, please refer to standard http://www.w3.org/TR/navigation-timing/
	this.submitClientPerformanceMetrics = function(){
		var networkTime = performance.timing.requestStart - performance.timing.domainLookupStart;
		var responseTime = performance.timing.responseEnd - performance.timing.requestStart;
		var domTime = performance.timing.domComplete - performance.timing.domLoading;
		var enduserTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
		var clientTiming = {networkTime: networkTime, responseTime: responseTime, domTime: domTime, enduserTime: enduserTime };
		var client = new XMLHttpRequest();
		client.open("GET", "/apm/browserData?d="+encodeURI(JSON.stringify(clientTiming)));
		client.send();
	}
	
};
document.onload = function(){
	APMPrototypeAgent.submitClientPerformanceMetrics();
}