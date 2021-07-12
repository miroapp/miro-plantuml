import * as React from 'react'
import * as ReactDOM from 'react-dom'
import axios, {AxiosResponse} from 'axios'

import './styles.css'

function App() {
    const [boardState, setBoardState] = React.useState({
        title: ''
    })

    const [authState, setAuthState] = React.useState({
        authorized: ''
    })

    function resetAuthState() {
        setAuthState({authorized: ""})
    }

    async function updateAuthState() {
        let authorized = await miro.isAuthorized()
        setAuthState({authorized: authorized ? "✅" : "❌"})
    }

    async function getBoardTitle() {
        const boardInfo = await miro.board.info.get()
        setBoardState({title: boardInfo.title})
    }

    async function deleteAllContent() {
        const allObjects = await miro.board.widgets.get()
        if (!allObjects.length) {
            alert('Nothing to delete, try to create some content first')
            return
        }
        await miro.board.widgets.deleteById(allObjects.map((object) => object.id))
        await miro.showNotification('Content has been deleted (you can undo this action)')
    }

    async function getScopes() {
        const scopes = await miro.currentUser.getScopes()
        console.error(`Scopes: ${scopes}`)
        await miro.showNotification(`Scopes: ${scopes}`)
    }

    async function getClientId() {
        const clientId = miro.getClientId()
        await miro.showNotification(`Client id : ${clientId}`)
    }

    async function getToken() {
        const token = await miro.getToken()
        await updateAuthState()
        console.error(`You should not use this method!!! Token is "${token}"`)
        alert(`You should not use this method!!! Token is "${token}"`)
    }

    async function getIdToken() {
        const token = await miro.getIdToken()
        await updateAuthState()
        console.error(`Id token is "${token}"`)
        await miro.showNotification(`Id token is ${token}`)
    }

    async function isAuthorized() {
        const isAuthorized = await miro.isAuthorized()
        await updateAuthState()
        console.error(`Is authorized: "${isAuthorized}"`)
        await miro.showNotification(`Is authorized ${isAuthorized}`)
    }

    async function authorize() {
        const redirectUrl = new URL(window.location.href)
        redirectUrl.pathname = "/install"

        const token = await miro.authorize({
            state: "test-state",
            redirect_uri: redirectUrl.href
        })
        await updateAuthState()
        console.error(`Authorize token: "${token}"`)
        alert(`You should not use this method!!! Authorize token: "${token}"`)
    }

    async function requestAuthorization() {
        const redirectUrl = new URL(window.location.href)
        redirectUrl.pathname = "/install"

        await miro.requestAuthorization({
            state: "test-state",
            redirect_uri: redirectUrl.href
        })
        await updateAuthState()
        console.error(`Authorization requested`)
    }

    async function callBackend() {
        const backendUrl = new URL(window.location.href)
        backendUrl.pathname = "/call"

        const token = await miro.getIdToken()
        resetAuthState()
        axios.get(backendUrl.href,
            {
                headers: {
                    "X-Miro-Token": token
                }
            })
            .then((response: AxiosResponse) => {
                console.error(`callBackend: "${response.data}"`)
                miro.showNotification(`callBackend: user name="${response.data.name}"`)
            })
            .catch((error) => {
                let message = error.message
                if (error.response) {
                    message = JSON.stringify(error.response.data)
                } else if (error.request) {
                    message = "request: " + error.request
                }

                console.error(`callBackend error: "${message}"`)
                alert(`callBackend: error "${message}"`)
            });
    }

    return (
        <div className="container centered">
            <div>Board title is: {boardState.title}</div>
            <br/>
            <button onClick={() => getBoardTitle()}>Get board title</button>
            <br/>
            <button onClick={() => deleteAllContent()}>Delete all content</button>
            <br/>
            <button onClick={() => getScopes()}>getScopes</button>
            <br/>
            <br/>
            <br/>
            <div>Authorized: {authState.authorized}</div>
            <br/>
            <button onClick={() => getClientId()}>getClientId</button>
            <br/>
            <button onClick={() => getToken()}><del>getToken</del></button>
            <br/>
            <button onClick={() => getIdToken()}>getIdToken</button>
            <br/>
            <button onClick={() => isAuthorized()}>isAuthorized</button>
            <br/>
            <button onClick={() => authorize()}><del>authorize</del></button>
            <br/>
            <button onClick={() => requestAuthorization()}>requestAuthorization</button>
            <br/>
            <button onClick={() => callBackend()}>call backend</button>
        </div>
    )
}

ReactDOM.render(<App/>, document.getElementById('root'))
