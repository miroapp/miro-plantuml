async function init() {
  miro.board.ui.on('icon:click', async () => {
    await miro.board.ui.openModal({
      url: 'app.html',
      width: 600,
      height: 410,
      fullscreen: false,
    });
  });
}

init();
