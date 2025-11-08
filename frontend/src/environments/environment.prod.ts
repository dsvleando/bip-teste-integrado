const globalEnv = (window as unknown as { __env?: { apiUrl?: string } }).__env;

export const environment = {
  production: true,
  apiUrl: globalEnv?.apiUrl ?? '/api/v1'
};
