import * as React from 'react'
import * as ReactDOM from 'react-dom'
import axios, {AxiosResponse} from 'axios'

import './styles.css'

function App() {
    const [state, setState] = React.useState({boardTitle: ''})

    async function getBoardTitle() {
        const boardInfo = await miro.board.info.get()
        setState({boardTitle: boardInfo.title})
    }

    async function deleteAllContent() {
        const allObjects = await miro.board.widgets.get()
        if (!allObjects.length) {
            alert('Nothing to delete, try to create some content first')
            return
        }
        await miro.board.widgets.deleteById(allObjects.map((object) => object.id))
        await miro.showNotification('Content has been deleted')
    }

    function getClientId() {
        const clientId = miro.getClientId()
        alert("Client id : " + clientId)
    }

    async function getToken() {
        const token = await miro.getToken()
        console.error("You should not use this method!!! " +
            "Token is \"" + token + "\"")
        alert("Token is " + token)
    }

    async function getIdToken() {
        const token = await miro.getIdToken()
        console.error("Id token is \"" + token + "\"")
        alert("Id token is " + token)
    }

    async function isAuthorized() {
        const isAuthorized = await miro.isAuthorized()
        console.error("Is authorized: \"" + isAuthorized + "\"")
        alert("Is authorized " + isAuthorized)
    }

    async function authorize() {
        const token = await miro.authorize({
            response_type: "code",
            state: "test-state"
        })
        console.error("Authorize token: \"" + token + "\"")
        alert("Authorize token: \"" + token + "\"")
    }

    async function callBackend() {
        const url = new URL(window.location.href)
        url.pathname = "/call"
        // url.search = ""

        const token = await miro.getIdToken()
        axios.get(url.href,
            {
                params: {
                    id_token: token
                }
            })
            .then((response: AxiosResponse) => {
                console.error("callBackend: \"" + response.data + "\"")
                alert("callBackend: user=" + response.data.user + "\n" +
                    "team=" + response.data.team)
            });
    }

    return (
        <div className="container centered">
            <button onClick={() => getBoardTitle()}>Get board title</button>
            <br/>
            <div>Board title is: {state.boardTitle}</div>
            <br/>
            <br/>
            <button onClick={() => deleteAllContent()}>Delete all content</button>
            <br/>
            <button onClick={() => getClientId()}>getClientId</button>
            <br/>
            <button onClick={() => getToken()}><del>getToken</del></button>
            <br/>
            <button onClick={() => getIdToken()}>getIdToken</button>
            <br/>
            <button onClick={() => isAuthorized()}>isAuthorized</button>
            <br/>
            <button onClick={() => authorize()}>authorize</button>
            <br/>
            <button onClick={() => callBackend()}>call backend</button>
        </div>
    )
}

ReactDOM.render(<App/>, document.getElementById('root'))
