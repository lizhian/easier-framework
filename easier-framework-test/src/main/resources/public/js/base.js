function getQueryVariable(variable) {
    const query = window.location.search.substring(1);
    const vars = query.split("&");
    for (let i = 0; i < vars.length; i++) {
        const pair = vars[i].split("=");
        if (pair[0] === variable) {
            return vars[i].substring(variable.length + 1);
        }
    }
    return false;
}

function getMessage(responseOrError, defaultMessage) {
    if (responseOrError.response && responseOrError.response.data && responseOrError.response.data.message) {
        return responseOrError.response.data.message
    }
    if (responseOrError.data && responseOrError.data.message) {
        return responseOrError.data.message
    }
    return defaultMessage;
}

function showErrorMessage(responseOrError) {
    console.log(responseOrError)
    var message = getMessage(responseOrError, "请求异常");
    antd.message.error(message);
}


/**
 * 响应拦截器
 */

axios.interceptors.response.use(
    response => {
        if (response.status === 200 && response.data && response.data.code === 200) {
            response.data.response = response
            return Promise.resolve(response.data);
        } else {
            return Promise.reject(response);
        }
    },

    // 可根据错误响应码判断状态，做出相应的处理
    error => {
        var status = error.response.status;
        if (status === 401) {
            var message = getMessage(error, "用户未登录或登录信息已过期");
            antd.Modal.error({
                content: message,
                closable: false,
                onOk() {
                    var redirect_uri = error.response.data.data.redirect_uri
                    const append_frontend_uri = error.response.data.data.append_frontend_uri
                    const encode_frontend_uri = error.response.data.data.encode_frontend_uri
                    if (append_frontend_uri) {
                        const frontend_uri = window.location.origin + window.location.pathname
                        if (encode_frontend_uri) {
                            redirect_uri = redirect_uri + encodeURIComponent(frontend_uri)
                        } else {
                            redirect_uri = redirect_uri + frontend_uri
                        }
                    }
                    window.location.href = redirect_uri
                },
            });
        }
        return Promise.reject(error)
    }
);

function apiGet(config) {
    if (!config.catch) {
        config.catch = showErrorMessage
    }
    axios.get(config.url)
        .then(config.then)
        .catch(config.catch)
        .finally(config.finally)
}

function apiPost(config) {
    if (!config.catch) {
        config.catch = showErrorMessage
    }
    axios
        .post(config.url, config.body)
        .then(config.then)
        .catch(config.catch)
        .finally(config.finally)
}
