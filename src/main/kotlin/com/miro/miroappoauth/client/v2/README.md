## Plant UML > Miro (V2 Connectors)

This app primarily demonstrates the import of Plant UML code to a diagram rendered using Miro components. However, it also highlights the use of Miro's REST API V2 [*Connector*](https://developers.miro.com/reference/create-connector) endpoints. 

While this app is complex, the sections which leverage the Miro REST connectors can be broken down more easily, and serve as a good example of our V2 REST connectors in action. 

### Prerequisites and Setup:

1. For the full set of prerequisites and project setup, please see the main README.md in the project's root folder.

### Miro REST Connectors

The following folders contain the code that leverages Miro's REST Connectors:
- `src/main/kotlin/com/miro/miroappoauth/client/v2`
- `src/main/kotlin/com/miro/miroappoauth/client/MiroPublicClientV2.kt`

Within `client/v2` the following files determine the style, shape, and position of the connectors:
- File: `ConnectorStyle.kt` determines the `startStrokeCap`, `endStrokeCap`, `strokeWidth`, `strokeStyle`, and `strokeColor` fields within the API's `style` object.
- File: `CreateConnectorReq.kt` determines the `startItem`, `endItem`, `shape`, and positioning (`snapTo` property) fields of the connector request.
- File: `LineShape.kt` determines the `shape` of the connectors (straight, elbow, curved)

The API is called explicitly in lines 69 - 81 of the file `MiroPublicClientV2.kt`