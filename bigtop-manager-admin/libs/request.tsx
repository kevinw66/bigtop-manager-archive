import {message} from "antd";
import {Response} from "next/dist/compiled/@edge-runtime/primitives/fetch";

export class APIResponse {
  success: boolean;
  data: any;

  constructor(success: boolean, data: any) {
    this.success = success;
    this.data = data;
  }

  static success(data: any): APIResponse {
    return new APIResponse(true, data);
  }

  static fail(): APIResponse {
    return new APIResponse(false, null);
  }
}

export default async function request(input: RequestInfo | URL, init?: RequestInit): Promise<APIResponse> {
  let headers: HeadersInit = init?.headers || {};
  if (init?.method === "POST" || init?.method === "PUT") {
    headers = {
      ...headers,
      "Content-Type": "application/json"
    }
  }

  const response: Response = await fetch(input, {
    ...init,
    headers
  });

  if (!response.ok) {
    message.error(response.statusText);
    return APIResponse.fail();
  }

  const json = await response.json();
  if (json.code !== 0) {
    message.error(json.message);
    return APIResponse.fail();
  }

  return APIResponse.success(json.data);
}