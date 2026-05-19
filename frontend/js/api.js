class ApiClient {
  constructor() {
    this.baseUrl = '/api/v1';
  }

  getToken() { return localStorage.getItem('accessToken'); }
  getRefreshToken() { return localStorage.getItem('refreshToken'); }
  setTokens(access, refresh) {
    localStorage.setItem('accessToken', access);
    if (refresh) localStorage.setItem('refreshToken', refresh);
  }
  clearTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userInfo');
  }
  isLoggedIn() { return !!this.getToken(); }

  async request(method, url, data, options = {}) {
    const headers = { 'Content-Type': 'application/json' };
    const token = this.getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;

    const config = { method, headers, ...options };
    if (data && method !== 'GET') config.body = JSON.stringify(data);

    const fullUrl = this.baseUrl + url;
    let resp = await fetch(fullUrl, config);

    if (resp.status === 401 && this.getRefreshToken()) {
      const refreshed = await this.refreshAccessToken();
      if (refreshed) {
        headers['Authorization'] = 'Bearer ' + this.getToken();
        config.headers = headers;
        resp = await fetch(fullUrl, config);
      }
    }

    if (resp.status === 204) return { code: 'Success', data: null };
    const json = await resp.json();
    if (!resp.ok) throw { status: resp.status, ...json };
    return json;
  }

  async refreshAccessToken() {
    try {
      const resp = await fetch(this.baseUrl + '/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: this.getRefreshToken() })
      });
      if (!resp.ok) { this.clearTokens(); return false; }
      const json = await resp.json();
      this.setTokens(json.data.accessToken);
      return true;
    } catch { this.clearTokens(); return false; }
  }

  async upload(url, formData) {
    const headers = {};
    const token = this.getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    const resp = await fetch(this.baseUrl + url, { method: 'POST', headers, body: formData });
    const json = await resp.json();
    if (!resp.ok) throw { status: resp.status, ...json };
    return json;
  }

  async download(url) {
    const headers = {};
    const token = this.getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    const resp = await fetch(this.baseUrl + url, { headers });
    if (!resp.ok) throw { status: resp.status, message: '下载失败' };
    return resp.blob();
  }

  get(url, params) {
    if (params) {
      const qs = Object.entries(params).filter(([,v]) => v !== undefined && v !== null && v !== '').map(([k,v]) => k + '=' + encodeURIComponent(v)).join('&');
      if (qs) url += '?' + qs;
    }
    return this.request('GET', url);
  }
  post(url, data) { return this.request('POST', url, data); }
  put(url, data) { return this.request('PUT', url, data); }
  delete(url) { return this.request('DELETE', url); }
}

window.api = new ApiClient();
