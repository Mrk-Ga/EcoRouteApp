from fastapi import FastAPI
from backend.routers import users, routes, gdpr

app = FastAPI()

app.include_router(users.router)
app.include_router(routes.router)
app.include_router(gdpr.router)

@app.get("/")
def root():
    return {"message": "EcoRouteApp Backend is running"}
