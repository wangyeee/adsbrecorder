var path = require('path');

module.exports = [
    {
        entry: './src/main/js/airline.js',
        devtool: 'sourcemaps',
        cache: true,
        debug: true,
        output: {
            path: __dirname,
            filename: './src/main/resources/static/built/airlineb.js'
        },
        module: {
            loaders: [
                {
                    test: path.join(__dirname, '.'),
                    exclude: /(node_modules)/,
                    loader: 'babel',
                    query: {
                        cacheDirectory: true,
                        presets: ['es2015', 'react']
                    }
                }
            ]
        }
    },
    {
        entry: './src/main/js/live.js',
        devtool: 'sourcemaps',
        cache: true,
        debug: true,
        output: {
            path: __dirname,
            filename: './src/main/resources/static/built/liveb.js'
        },
        module: {
            loaders: [
                {
                    test: path.join(__dirname, '.'),
                    exclude: /(node_modules)/,
                    loader: 'babel',
                    query: {
                        cacheDirectory: true,
                        presets: ['es2015', 'react']
                    }
                }
            ]
        }
    },
    {
        entry: './src/main/js/map.js',
        devtool: 'sourcemaps',
        cache: true,
        debug: true,
        output: {
            path: __dirname,
            filename: './src/main/resources/static/built/mapb.js'
        },
        module: {
            loaders: [
                {
                    test: path.join(__dirname, '.'),
                    exclude: /(node_modules)/,
                    loader: 'babel',
                    query: {
                        cacheDirectory: true,
                        presets: ['es2015', 'react']
                    }
                }
            ]
        }
    },
    {
        entry: './src/main/js/history.js',
        devtool: 'sourcemaps',
        cache: true,
        debug: true,
        output: {
            path: __dirname,
            filename: './src/main/resources/static/built/histb.js'
        },
        module: {
            loaders: [
                {
                    test: path.join(__dirname, '.'),
                    exclude: /(node_modules)/,
                    loader: 'babel',
                    query: {
                        cacheDirectory: true,
                        presets: ['es2015', 'react']
                    }
                }
            ]
        }
    }
];
