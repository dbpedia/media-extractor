/**
 *
 */
package leandro.mediaextractor
import scala.math.cos
import java.net.URLEncoder
import scala.collection.mutable.Map
import scala.xml.Utility.escape
import scala.io.Source
import com.codahale.jerkson.Json._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.vocabulary._

/**
 * @author Leandro Doctors
 *
 */

trait GeoCalc {

	def degreesToRadians(that: Double): Double = {
			that * 0.01745329252
	}
	def longPerKmAt(lat: Double): Double = {
			1 / 111.321 * cos(degreesToRadians(lat))
	}
	def latPerKm: Double = {
				1 / 111.00
			}
}

case class CustomError(code: Int, message: String)

case class flickrService(
		apiKey: String,
		resultsPerQuery: Int,
		geoCalc: GeoCalc,
		error: CustomError,
		onlyCCDeriv: Boolean) {

	def getFlickrPhotos(
			topic: String,
			lat: Double,
			long: Double,
			searchRadiusInKm: Double = 5.0) =
		{

		var params = Map( /* "params" array starts here */
				"api_key" -> "My API Key",
				"method" -> "flickr.photos.search",
				"format" -> "php_serial",
				"resultsPerQuery" -> "30",
				//      'tags'        => str_replace(' ', ',', $label)
				//      'text'        => $topic,
				"sortMode" -> "relevance",

				/**
				 * Geo queries require some sort of limiting agent in order to prevent the database from crying. This is basically like the check against "parameterless searches" for queries without a geo component.
				 * A tag, for instance, is considered a limiting agent as are user defined min_date_taken and min_date_upload parameters � If no limiting factor is passed we return only photos added in the last 12 hours (though we may extend the limit in the future).
				 */
				"minTakenDate" -> "1800-01-01 00:00:00")

				if (onlyCCDeriv) {
					val license = "4,2,1,5" /* CC-BY, CC-NC, CC-NC-SA, CC-SA */
							//		if ($topic != '')
							//			$params['text'] = '"' . $topic . '"';
				}

		if ((lat != null) && (long != null) && (searchRadiusInKm != null)) {
			/* minimum_longitude, minimum_latitude, maximum_longitude, maximum_latitude */
			val lat_min = lat - (geoCalc.latPerKm * searchRadiusInKm)
					val lat_max = lat + (geoCalc.latPerKm * searchRadiusInKm)
					val long_min = long - (geoCalc.longPerKmAt(lat) * searchRadiusInKm)
					val long_max = long + (geoCalc.longPerKmAt(lat) * searchRadiusInKm)
		}

		val encoded_params = (for ((k, v) <- params)
			yield URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8")).mkString("&")

			val url = "http://api.flickr.com/services/rest/?" + encoded_params
			val rsp = Source.fromFile(url).mkString("")
			var rsp_obj = generate(rsp)
			var photos = Map();

				if (!rsp_obj.isEmpty()) { // && is_array($rsp_obj['photos']['photo']))
					for ((k, v) <- photos) //each ($rsp_obj['photos']['photo'] as $photo)
					{
						/* Enhance with URLs */

						/* TODO: not implemented yet */
						//				v("imgsmall") = "http://farm" + v("farm")+ ".static.flickr.com/" + v("server")+"/"+
						//				v("id")+ "_"+ v("secret")+"_m.jpg"
						//				v->"flickrpage" = "http://www.flickr.com/photos/"+ v-> "owner"+"/" + v->"id"
						//array_push($photos, $photo);
					}

					if (photos.isEmpty && (rsp_obj -> "stat" == "fail")) {
						//error.code = (rsp_obj->"code")
						//error.message = rsp_obj-> "message"
					}

					photos

				}
		}
}

trait IntputOutput {

	def clientAcceptsRDF: Boolean = {

		val request = new HttpGet("http://alvinalexander.com/")
		val client = HttpClientBuilder.create().build()
		val response = client.execute(request)

		//if the "format" field is set in the request, check it is set to "rdf"; otherwise, check whether the client accepts "application/rdf+xml"
		val result = (request.getFirstHeader("format").equals("rdf") || response.getFirstHeader("Accept").equals("application/rdf+xml"))
		result
}

def dump(file: String, title: String = "") {
	if (title != "")
		println("<h1>" + title + "</h1>")
		println("<pre>" + escape(file) + "</pre>");
}

def generateRDF(title: String = "Serialized RDF"): String = {
		if (!clientAcceptsRDF) {
			//FIXME!!
			dump("this", title).toString()
		} else {
			// This code is adapted from a Scala recipe

			// TODO: properly define the URLs
			// FIX: replace "GET" by "POST"

			val url = "http://localhost:9001/baz"
					val httpGet = new HttpGet(url)
			val date = new java.util.Date

			// set the desired header values

			httpGet.setHeader("Content-Type", "application/rdf+xml;charset=utf-8")
			httpGet.setHeader("Cache-Control", "no-cache, must-revalidate")
			httpGet.setHeader("Expires", date.toString())

			httpGet.toString()
		}
}

def wikipediaEncode(page_title: String): String = {
		page_title.replaceAll("%2F", "/").replaceAll("%3A", ":")
}

def parameterize(
		html_template: String,
		params: Map[String, String]): String = {
		for ((k, v) <- params)
			html_template.replaceAll(k, v)
			html_template
}

}

object flickrWrappr {

	//TODO: implement this function
	//  def DBPediaLookup(
	//			item: String,
	//			resultsLabel: String,
	//			resourceFound: Boolean): Model {
	//		
	//	}

	//TODO	def generateLinks:File


	val FLICKRSERVICE = new flickrService(FLICKR_API_KEY, NUM_RESULTS_PER_QUERY, ONLY_CC_DERIV)

	def geoLookup(
			lat: Float,
			long: Float,
			radius: Float,
			resultsLabel: String): Model = {

		val resURI: String = DBPEDIA_URI_ROOT + wikipediaEncode(_REQUEST("item"));
	val locationURI: String = FLICKRWRAPPR_LOCATION_URI_ROOT + _REQUEST("lat") + "/" + _REQUEST("long") + "/" + _REQUEST("radius")
			val dataURI: String = FLICKRWRAPPR_LOCATION_DATA_URI_ROOT + _REQUEST("lat") + "/" + _REQUEST("long") + "/" + _REQUEST("radius")

			/* Initialize result model */
			var resultModel = ModelFactory.createDefaultModel()

			val NsPrefixes = (
					"foaf" -> "http://xmlns.com/foaf/0.1/",
					"dcterms" -> "http://purl.org/dc/terms/",
					"rdfs" -> "http://www.w3.org/2000/01/rdf-schema#",
					//"geonames"-> "http://www.geonames.org/ontology#",
					"geo" -> "http://www.w3.org/2003/01/geo/wgs84_pos#",
					"georss" -> "http://www.georss.org/georss/",
					"rdf-syntax-ns"->"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
					"xml-schema"->"http://www.w3.org/2001/XMLSchema#",
					"purl"->"http://purl.org/dc/terms/")

					resultModel.setNsPrefixes(NsPrefixes)

					/* Perform flickr search */
					var flickrPhotos = FLICKRSERVICE.getFlickrPhotos ~> ("", lat, long, radius / 1000);

					/* Process found photos */
					flickrPhotos.foreach { (flickrPhoto: Any) =>
					{
						/* Provide the picture itself (small version) */
						resultModel.createResource(locationURI)
						.addProperty(locationURI+"depiction")
						.addProperty(flickrPhoto("imgsmall"))

						/* Provide its page on flickr */
						resultModel.createResource(flickrPhoto("imgsmall")
								.addProperty(locationURI+"page")
								.addProperty(flickrPhoto("flickrpage"))
					}
					}

					if (resultModel.size() > 0) {

						/* Add metadata for location */

						resultModel.createResource(locationURI)
						.addProperty(NsPrefixes("rdf-syntax-ns")+"type")
						.addProperty(NsPrefixes("geo")+"SpatialThing")

						val latLiteral = resultModel.createLiteral(lat,NsPrefixes(xml-schema)+"#float")
						resultModel.createResource(locationURI)
						.addProperty(NsPrefixes("geo")+"lat")
						.addProperty(latLiteral)

						val longLiteral = resultModel.createLiteral(long,NsPrefixes(xml-schema)+"#float")
						resultModel.createResource(locationURI)
						.addProperty(NsPrefixes("geo")+"long")
						.addProperty(longLiteral)

						val radiusLiteral = resultModel.createLiteral(radius,NsPrefixes(xml-schema)+"#double")
						resultModel.createResource(locationURI)
						.addProperty(NsPrefixes("georss")+"radius")
						.addProperty(radiusLiteral)


						/* Add metadata for document */

						resultModel.createResource(dataURI)
						.addProperty(NsPrefixes("rdf-syntax-ns")+"type")
						.addProperty(NsPrefixes("foaf")+"Document")

						val resultsLabel = "Photos taken within " + radius + " meters of geographic location lat=" + lat + " long=" + long
						resultModel.createResource(dataURI)
						.addProperty(NsPrefixes("rdfs")+"label")
						.addProperty(resultsLabel,"en")

						resultModel.createResource(dataURI)
						.addProperty(NsPrefixes("foaf")+"primaryTopic")
						.addProperty(locationURI)

						resultModel.createResource(dataURI)
						.addProperty(NsPrefixes("purl")+"licence")
						.addProperty(FLICKR_TOS_URL)

						resultModel.createResource(dataURI)
						.addProperty(NsPrefixes("foaf")+"maker")
						.addProperty(FLICKRWRAPPR_HOMEPAGE)

						resultModel.createResource(FLICKRWRAPPR_HOMEPAGE)
						.addProperty(NsPrefixes("rdfs")+"label")
						.addProperty("flickr(tm) wrappr","en")
					}

					resultModel
	}

	def main(args: Array[String]): Unit = {
		val FLICKRSERVICE = new flickrService __construct(FLICKR_API_KEY, NUM_RESULTS_PER_QUERY, ONLY_CC_DERIV);

		val resultModel match {
		  case Set("item") => DBpediaLookup(_REQUEST("item"), resultsLabel, resourceFound)
		  case _ =>  geoLookup(_REQUEST("lat"), _REQUEST("long"), _REQUEST("radius"), resultsLabel)
		  }
		
		if (resultModel.size() == 0) {
				if (FLICKRSERVICE->errCode.isnull) {
					setHeader("HTTP/1.1 500 Internal Server Error");
					println(parameterize(HTML_TEMPLATE_ERROR, 
							Map("TITLE" -> "Flickr error #" + FLICKRSERVICE -> errCode,
									"MESSAGE" -> FLICKRSERVICE -> errMsg,
									"FLICKRWRAPPR_HOMEPAGE" -> FLICKRWRAPPR_HOMEPAGE)))
				}
				else {
					setHeader("HTTP/1.1 404 Not Found");

					if (!isset(_REQUEST("item"))) {
						println(parameterize(HTML_TEMPLATE_ERROR, 
								Map("TITLE" -> "Sorry, no photos found in the specified area",
										"MESSAGE" -> "Please extend the search radius or verify the coordinates!",
										"FLICKRWRAPPR_HOMEPAGE" -> FLICKRWRAPPR_HOMEPAGE)))
					}
					else if (resourceFound) {
						println(parameterize(HTML_TEMPLATE_ERROR, 
								Map("TITLE" -> "Sorry, no photos found for DBpedia.org resource " +& _REQUEST("item"),
										"MESSAGE" -> "You specified a valid resource, but we were unable to locate any photos for it!",
										"FLICKRWRAPPR_HOMEPAGE" -> FLICKRWRAPPR_HOMEPAGE)))
					}
					else
					{
						println(parameterize(HTML_TEMPLATE_ERROR, 
								Map("TITLE" -> "DBpedia.org resource " +& _REQUEST("item") +& " does not exist",
										"MESSAGE" -> "Sorry, we were unable to find a resource at <a href="http://dbpedia.org">http://dbpedia.org</a> that matches your request!",
											"FLICKRWRAPPR_HOMEPAGE" -> FLICKRWRAPPR_HOMEPAGE)))
					}
				}
				exit();
			}
			else {
				/* Output model */
				var ser = new RdfSerializer()
				val rdf = ser.serialize->(resultModel)

				if (clientAcceptsRDF())
					outputRdf(rdf)
				else {
					/* Prepare HTML version */
					var res = resultModel.sparqlQuery->(QUERY_DISPLAY)
					photos_html = ""

					res.foreach{ (line : Any) => {
						photos_html +=& ( parameterize(HTML_TEMPLATE_PHOTO,
								Map("SRC" -> line("?img").getURI(),
										"HREF" -> line("?flickrpage").getURI())))
					} }    	
					println((parameterize(HTML_TEMPLATE_RESULTS,
							Map("TITLE" -> resultsLabel,
									"RDF" -> htmlspecialchars(rdf),
									"PHOTOS_HTML" -> photos_html,
									"FLICKRWRAPPR_HOMEPAGE" -> FLICKRWRAPPR_HOMEPAGE,
									"FLICKR_TOS_URL" -> FLICKR_TOS_URL))))
				}
			}

	}
}
