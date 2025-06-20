from locust import task
from locust.contrib.fasthttp import FastHttpUser

class ProductApiUser(FastHttpUser):
    host = "http://localhost:8080"

    @task
    def get_all_products(self):
        url = "/api/products"

        with self.client.get(url, name="Get All Products") as response:
            if response.status_code != 200:
                response.failure(f"Got non-200 status code: {response.status_code}")
