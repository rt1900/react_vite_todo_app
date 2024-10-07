module.exports = {
  component: {
    devServer: {
      framework: "react",
      bundler: "vite",
    },
  },

  e2e: {
    baseUrl: 'http://localhost:5173', // URL of my frontend application
    setupNodeEvents(on, config) {
      // Node event settings, if necessary
    },
  },
};
