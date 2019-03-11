const { resolve } = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = {
  entry: './index',
  context: resolve(__dirname, "../src"),
  resolve: {
    extensions: [".ts", ".tsx", ".js", ".json"],
  },
  output: {
    filename: 'bundle.js',
    path: resolve(__dirname, "../dist"),
  },
  module: {
    rules: [
      { test: /\.tsx?$/, loader: "ts-loader" }
    ]
  },
  // This will be overridden by a runtime parameter when building
  mode: "development",
  plugins: [
    new HtmlWebpackPlugin({
      title: 'My First Realm App',
    }),
  ],
};
