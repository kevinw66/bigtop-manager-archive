import request, {APIResponse} from "@/libs/request";
import {AppRouterInstance} from "next/dist/shared/lib/app-router-context";

interface LoginReq {
  username: string;
  password: string;
}

export async function login(router: AppRouterInstance, req: LoginReq): Promise<void> {
  const response: APIResponse = await request("/api/login", {
    method: "POST",
    body: JSON.stringify(req),
  })

  if (response.success) {
    router.push("/admin")
  }
}