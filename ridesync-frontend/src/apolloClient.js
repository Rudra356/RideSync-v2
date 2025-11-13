// src/apolloClient.js
import { ApolloClient, InMemoryCache } from "@apollo/client";

const client = new ApolloClient({
  uri: "http://localhost:8080/graphql", // 🔧 change if backend is on a different port
  cache: new InMemoryCache(),
});

export default client;
