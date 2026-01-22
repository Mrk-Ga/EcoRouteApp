from fastapi import FastAPI
from backend.routers import users, routes, gdpr, reports, history, admin
app = FastAPI()

app.include_router(users.router)
app.include_router(routes.router)
app.include_router(gdpr.router)
app.include_router(reports.router)
app.include_router(history.router)
app.include_router(admin.router)

@app.get("/")
def root():
    return {"message": "EcoRouteApp Backend is running"}
