import * as React from 'react';
import ReactDOM from 'react-dom';
import cn from 'classnames'
import axios, {AxiosResponse} from "axios";

function App() {
    const [isRendering, setIsRendering] = React.useState(false);
    const [text, setText] = React.useState('');
    const [link, setLink] = React.useState('');

    async function getPreviewUrl() {
        setIsRendering(true)
        const backendUrl = new URL(window.location.href)
        backendUrl.pathname = "/get-preview-url"
        const urlWithParams = backendUrl.href + "?payload=" + encodeURIComponent(text);

        axios.get(urlWithParams,
            {
                headers: {
                }
            })
            .then((response: AxiosResponse) => {
                console.error(`callBackend: "${response.data}"`)
                setLink(response.data);
                setIsRendering(false)
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

    async function submitPreviewImage() {
        setIsRendering(true)
        const backendUrl = new URL(window.location.href)
        backendUrl.pathname = "/submit-preview-image"

        const boardInfo = await miro.board.getInfo()
        console.error("boardId " + boardInfo.id)
        const token = await miro.board.getIdToken()
        console.error("Token " + token)
        axios.post(backendUrl.href,
            {
                "boardId" : boardInfo.id,
                "payload": text
            }, {
                headers: {
                    "X-Miro-Token": token
                }
            })
            .then((response: AxiosResponse) => {
                setIsRendering(false)
                console.error(`callBackend: "${response.data}"`)
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

    async function submitPlantuml() {
        setIsRendering(true)
        const backendUrl = new URL(window.location.href)
        backendUrl.pathname = "/submit-plantuml"

        const boardInfo = await miro.board.getInfo()
        console.error("boardId " + boardInfo.id)
        const token = await miro.board.getIdToken()
        console.error("Token " + token)
        axios.post(backendUrl.href,
            {
                "boardId" : boardInfo.id,
                "payload": text
            }, {
                headers: {
                    "X-Miro-Token": token
                }
            })
            .then((response: AxiosResponse) => {
                setIsRendering(false)
                miro.board.ui.closeModal()
                console.error(`callBackend: "${response.data}"`)
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
        <div className="grid wrapper">
            <h2 className="h1">PlantUML</h2>
            <div className="cs1 ce12">
                <a className="link link-primary" href="https://plantuml.com/" target="_blank">Documentation</a>
            </div>
            <div className="cs1 ce12 form-group">
                <textarea value={text} onChange={(e) => setText(e.target.value)} className="textarea" placeholder="Code" rows={10} spellCheck={false}></textarea>
            </div>
            <div className="cs1 ce6 flex">
                <button
                    className={cn('button', 'button-primary', {
                        'button-loading': isRendering
                    })}
                    onClick={getPreviewUrl}
                    type="button"
                    disabled={isRendering}
                >
                    {isRendering ? '' : 'Preview'}
                </button>
                <button
                    className={cn('button', 'button-primary', {
                        'button-loading': isRendering
                    })}
                    onClick={submitPlantuml}
                    type="button"
                    disabled={isRendering}
                >
                    {isRendering ? '' : 'Add to board'}
                </button>
            </div>
            {link && (
                <div className="cs1 ce12">
                    <a href={link} className="link link-primary" target="_blank" style={{ marginRight: '12px'}}>Preview link</a>
                    <a className="link link-primary" onClick={submitPreviewImage}>Add as image to board</a>
                </div>
            )}
        </div>
    );
}

ReactDOM.render(<App/>, document.getElementById('root'));
