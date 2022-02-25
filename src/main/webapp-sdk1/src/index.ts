import icon from './icon.svg'

miro.onReady((): void => {
  let appName = new URLSearchParams(window.location.search).get("appName");
  let title = appName == null ? 'miro-app-oauth' : appName;
  miro.initialize({
    extensionPoints: {
      toolbar: {
        title: title,
        toolbarSvgIcon: icon,
        librarySvgIcon: icon,
        async onClick(): Promise<void> {
          // Remember that 'app.html' resolves relative to index.js file. So app.html have to be in the /dist/ folder.
          await miro.board.ui.openLibrary('/webapp-sdk1/app.html', {title: title})
        },
      },
    },
  })
})
