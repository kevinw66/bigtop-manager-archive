"use client"

import React, {useEffect} from 'react';
import {useRouter} from "next/navigation";
import useSWR from "swr";
import fetcher from "@/libs/fetcher";

const App: React.FC = () => {
  const router = useRouter();
  const { data, error, isLoading } = useSWR("/api/clusters", fetcher)

  useEffect(() => {
    if (!data) {
      // data is undefined when first time loaded
      return;
    }

    if (data.code === 10000) {
      // code 10000 means not login
      router.push("/login");
    } else {
      router.push("/admin");
    }
  }, [data]);

  if (error) return <div>failed to load</div>
  if (isLoading) return <div>loading111...</div>

  return (
    <div>loading222...</div>
  );
};

export default App;