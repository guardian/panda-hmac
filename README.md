> [!WARNING]  
> The panda-hmac modules have been moved to [guardian/pan-domain-authentication](https://github.com/guardian/pan-domain-authentication). This repo is now out of date.

# Play HMAC

Some useful `AuthActions` for working with machine/user auth based of HMAC for the robots and cookies for the monkeys.

## Quick Rundown
We use HMAC-SHA-256

## How to use

Assuming that you are using `pan-domain-auth-play` and have already set up `pan-domain-auth` 
(see the instructions on those repos for details on how to do this), then it should be as simple as:

#### build.sbt
```
libraryDependencies += "com.gu" %% "panda-hmac-play_2.8" % "2.0.0"
```

#### controller
```scala
import com.gu.pandahmac.HMACAuthActions

// ...

@Singleton
class MyController @Inject() (
    override val config: Configuration,
    override val controllerComponents: ControllerComponents,
    override val wsClient: WSClient,
    override val refresher: InjectableRefresher
) extends AbstractController(controllerComponents)
    with PanDomainAuthActions
    with HMACAuthActions {

  override def secretKeys = List("currentSecret") // You're likely to get your secret from configuration or a cloud service like AWS Secrets Manager

  def myApiActionWithBody = APIHMACAuthAction.async(circe.json(2048)) { request => 
    // ... do something with the request
  }

  def myRegularAction = HMACAuthAction {}

  def myRegularAsyncAction = HMACAuthAction.async {}
}
```

## How to setup a machine client

There are example clients for Scala, Javascript and Python in the `examples/` directory.

Each client needs a copy of the shared secret, defined as "mysecret" in the controller example above.
Each request needs a standard (RFC-7231) HTTP Date header, and an authorization digest that is calculated like this:

1. Make a "string to sign" consisting of the HTTP Date and the Path part of the URI you're trying to access, 
seperated by a literal newline (unix-style, not CRLF)
2. Calculate the HMAC digest of the "string to sign" using the shared secret as a key and the HMAC-SHA-256 algorithm
3. Base64 encode the binary output of the HMAC digest to get a random-looking string
4. Add the HTTP date to the request headers with the header name **'X-Gu-Tools-HMAC-Date'**
5. Add another header called **'X-Gu-Tools-HMAC-Token'** and set its value to the literal string **HMAC** followed by a
 space and the digest, like this: `X-Gu-Tools-HMAC-Token: HMAC boXSTNumKWRX3eQk/BBeHYk`
6. Send the request and the server should respond with a success.
7. The default allowable clock skew is 5 minutes, if you have problems then this is the first thing to check.

## Testing HMAC-authenticated endpoints in isolation

[Postman]([url](https://www.postman.com/)) is a common environment for testing HTTP requests. We can add a [pre-request script]([url](https://learning.postman.com/docs/writing-scripts/pre-request-scripts/)) that automatically adds HMAC headers when we hit send.

<details>
<summary>Pre-request script</summary>
  
```js
const URL = require("url");

const uri = pm.request.url.toString();
const secret = "Secret goes here :)";

const httpDate = new Date().toUTCString();
const path = new URL.parse(uri).path;
const stringToSign = `${httpDate}\n${path}`;
const stringToSignBytes = CryptoJS.enc.Utf8.parse(stringToSign);
const secretBytes = CryptoJS.enc.Utf8.parse(secret);

const signature = CryptoJS.enc.Base64.stringify(CryptoJS.HmacSHA256(stringToSignBytes, secretBytes));
const authToken = `HMAC ${signature}`;

pm.request.headers.add({ key: 'X-Gu-Tools-HMAC-Date', value: httpDate });
pm.request.headers.add({ key: 'X-Gu-Tools-HMAC-Token', value: authToken });
```

</details>


