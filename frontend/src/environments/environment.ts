const globalEnv = (window as unknown as { __env?: { apiUrl?: string } }).__env;

export const environment = {
  production: false,
  apiUrl: globalEnv?.apiUrl ?? 'http://localhost:8080/api/v1'
};
