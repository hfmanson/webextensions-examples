const
	target = "http://mansoft.nl/xslt-browser-plugin/*.xml"
	/*
	 On startup, connect to the "xslt" app.
	 */
	, port = browser.runtime.connectNative("xslt")
	;

function listener(details) {
	const
		filter = browser.webRequest.filterResponseData(details.requestId)
		, decoder = new TextDecoder("utf-8")
		, encoder = new TextEncoder()
		, url = details.url
		, baseURL = url.slice(0, url.lastIndexOf("/") + 1)
		, portListener = event => {
			console.log(event);
			const result = event.result;
			if (result) {
				filter.write(encoder.encode(result));
				filter.close();
				port.onMessage.removeListener(portListener);
			}
			if (event.fetch) {
				const fetchURL = baseURL + event.fetch;
				console.log(fetchURL);
				fetch(fetchURL)
						.then(response => {
							console.log(response.headers.get("Content-Type"));
							return response.text();
						})
						.then(text => {
							console.log(text);
							port.postMessage(
									{
										"data": text
									});
						});
			}
		}
	;
	let
		xml
		;

	filter.onstart = event => {
		console.log("onstart");
		xml = "";
	};
	filter.ondata = event => {
		console.log("ondata");
		xml += decoder.decode(event.data, {stream: true});
	};
	filter.onstop = event => {
		const
			parser = new DOMParser()
			, xmldoc = parser.parseFromString(xml, "application/xml")
			, result = xmldoc.evaluate(
					"/processing-instruction('xml-stylesheet')",
					xmldoc,
					null,
					XPathResult.FIRST_ORDERED_NODE_TYPE
					)
			;
				
		console.log("onstop");
		console.log(xml);
		if (result) {
			const
				processing_instruction = parser.parseFromString("<processing-instruction " + result.singleNodeValue.nodeValue + "/>", "application/xml")
				, href = processing_instruction.documentElement.getAttribute("href")
				;
				
			fetch(baseURL + href)
				.then(response => {
					const headers = response.headers;
					const content_type = headers.get("Content-Type");
					console.log(content_type);
					return response.text();
				})
				.then(stylesheet => {
					console.log(stylesheet);
					port.onMessage.addListener(portListener);
					port.postMessage(
							{
								"transform": xml,
								"stylesheet": stylesheet
							});
				});
		}
	};

	return {};
}


browser.webRequest.onBeforeRequest.addListener(
	listener,
	{urls: [target], types: ["main_frame"]},
	["blocking"]
);
