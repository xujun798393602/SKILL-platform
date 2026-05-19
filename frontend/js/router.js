/* Hash-based SPA Router */
window.Router = (function() {
    const routes = {};
    let current = null;

    function register(path, handler) {
        routes[path] = handler;
    }

    function navigate(path) {
        window.location.hash = '#' + path;
    }

    function getHash() {
        return window.location.hash.slice(1) || '/login';
    }

    function matchRoute(hash) {
        // Exact match first
        if (routes[hash]) return { handler: routes[hash], params: {} };

        // Pattern match (e.g., /skills/:id)
        for (const pattern in routes) {
            const patternParts = pattern.split('/');
            const hashParts = hash.split('/');
            if (patternParts.length !== hashParts.length) continue;

            const params = {};
            let match = true;
            for (let i = 0; i < patternParts.length; i++) {
                if (patternParts[i].startsWith(':')) {
                    params[patternParts[i].slice(1)] = hashParts[i];
                } else if (patternParts[i] !== hashParts[i]) {
                    match = false;
                    break;
                }
            }
            if (match) return { handler: routes[pattern], params };
        }
        return null;
    }

    function handleRoute() {
        const hash = getHash();
        const result = matchRoute(hash);

        if (!result) {
            navigate('/login');
            return;
        }

        // Auth guard: redirect to login if no token (except for /login)
        if (hash !== '/login' && !localStorage.getItem('accessToken')) {
            window.location.hash = '#/login';
            window.location.reload();
            return;
        }

        current = hash;

        // Login page: always render into #app (no layout)
        if (hash === '/login') {
            document.getElementById('app').innerHTML = '';
            result.handler(document.getElementById('app'), result.params);
            return;
        }

        const container = document.getElementById('page-content');
        if (container) {
            container.innerHTML = '';
            result.handler(container, result.params);
        } else {
            // No layout yet, reload to let boot() render it
            window.location.reload();
        }
    }

    window.addEventListener('hashchange', handleRoute);

    return {
        register,
        navigate,
        handleRoute,
        getCurrent() { return current; }
    };
})();
