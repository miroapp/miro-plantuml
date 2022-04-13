import * as React from 'react';
import ReactDOM from 'react-dom';
import cn from 'classnames'

function App() {
  const [isRendering, setRendering] = React.useState(false);

  async function addSomeElements() {
    setRendering(true);

    const shape1 = await miro.board.createShape({
      content: 'Hello, World!',
      shape: 'cloud'
    });

    await miro.board.createShape({
      content: 'See ya',
      shape: 'hexagon',
      x: shape1.x + 200,
      y: shape1.y
    });

    setRendering(false);
    await miro.board.ui.closeModal();
  }

  return (
    <div className="grid wrapper">
      <h1 className="h1">Mermaid</h1>
      <div className="cs1 ce12">
        <a className="link link-primary" href="https://github.com/mermaid-js/mermaid" target="_blank">Documenation</a>
      </div>
      <div className="cs1 ce12 form-group">
          <textarea className="textarea" placeholder="Code" rows={10} spellCheck={false}></textarea>
      </div>
      <div className="cs1 ce12">
        <button
          className={cn('button', 'button-primary', {
            'button-loading': isRendering
          })}
          onClick={addSomeElements}
          type="button"
          disabled={isRendering}
        >
          {isRendering ? '' : 'Render'}
        </button>
      </div>
    </div>
  );
}

ReactDOM.render(<App />, document.getElementById('root'));
