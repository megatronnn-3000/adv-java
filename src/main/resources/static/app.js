const API = "/api";

// ---------- helpers ----------
async function api(path, method = "GET", body) {
    const res = await fetch(API + path, {
        method,
        headers: body ? { "Content-Type": "application/json" } : {},
        body: body ? JSON.stringify(body) : undefined
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || "Something went wrong");
    return data;
}

function showMessage(text, type = "success") {
    const box = document.getElementById("message");
    box.textContent = text;
    box.className = "message " + type;
    clearTimeout(showMessage.timer);
    showMessage.timer = setTimeout(() => box.classList.add("hidden"), 3500);
}

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

const rs = n => "Rs." + Number(n).toFixed(2);

// ---------- tabs ----------
document.querySelectorAll(".tab").forEach(btn => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".tab").forEach(b => b.classList.remove("active"));
        document.querySelectorAll(".panel").forEach(p => p.classList.add("hidden"));
        btn.classList.add("active");
        document.getElementById(btn.dataset.tab).classList.remove("hidden");
        refreshAll();
    });
});

// ---------- vehicles ----------
async function loadVehicles() {
    const onlyAvailable = document.getElementById("onlyAvailable").checked;
    const vehicles = await api("/vehicles?available=" + onlyAvailable);
    document.getElementById("vehicleTable").innerHTML = vehicles.length
        ? vehicles.map(v => `
            <tr>
                <td>${v.id}</td>
                <td>${v.type}</td>
                <td>${escapeHtml(v.brand)}</td>
                <td>${escapeHtml(v.model)}</td>
                <td>${rs(v.ratePerDay)}</td>
                <td><span class="badge ${v.available ? "green" : "red"}">${v.available ? "Available" : "Rented"}</span></td>
            </tr>`).join("")
        : `<tr><td colspan="6" class="muted">No vehicles to show.</td></tr>`;
}

document.getElementById("onlyAvailable").addEventListener("change", loadVehicles);

document.getElementById("vehicleForm").addEventListener("submit", async e => {
    e.preventDefault();
    const f = e.target;
    try {
        const v = await api("/vehicles", "POST", {
            type: f.type.value,
            brand: f.brand.value,
            model: f.model.value,
            ratePerDay: Number(f.ratePerDay.value)
        });
        showMessage(`Vehicle added with ID ${v.id}`);
        f.reset();
        loadVehicles();
    } catch (err) {
        showMessage(err.message, "error");
    }
});

// ---------- customers ----------
async function loadCustomers() {
    const customers = await api("/customers");
    document.getElementById("customerTable").innerHTML = customers.length
        ? customers.map(c => `
            <tr><td>${c.id}</td><td>${escapeHtml(c.name)}</td><td>${escapeHtml(c.phone)}</td></tr>`).join("")
        : `<tr><td colspan="3" class="muted">No customers registered.</td></tr>`;
}

document.getElementById("customerForm").addEventListener("submit", async e => {
    e.preventDefault();
    const f = e.target;
    try {
        const c = await api("/customers", "POST", { name: f.name.value, phone: f.phone.value });
        showMessage(`Customer registered with ID ${c.id}`);
        f.reset();
        loadCustomers();
    } catch (err) {
        showMessage(err.message, "error");
    }
});

// ---------- rentals ----------
let availableVehicles = [];

async function loadRentForm() {
    const [customers, vehicles] = await Promise.all([api("/customers"), api("/vehicles?available=true")]);
    availableVehicles = vehicles;
    document.getElementById("rentCustomer").innerHTML =
        `<option value="">Select customer</option>` +
        customers.map(c => `<option value="${c.id}">${c.id} - ${escapeHtml(c.name)}</option>`).join("");
    document.getElementById("rentVehicle").innerHTML =
        `<option value="">Select vehicle</option>` +
        vehicles.map(v => `<option value="${v.id}">${v.id} - ${escapeHtml(v.brand)} ${escapeHtml(v.model)} (${rs(v.ratePerDay)}/day)</option>`).join("");
    updateEstimate();
}

function updateEstimate() {
    const f = document.getElementById("rentalForm");
    const v = availableVehicles.find(x => x.id === f.vehicleId.value);
    const days = Number(f.days.value);
    document.getElementById("estimate").textContent =
        v && days > 0 ? `Estimated total: ${rs(v.ratePerDay * days)}` : "";
}

document.getElementById("rentVehicle").addEventListener("change", updateEstimate);
document.querySelector("#rentalForm [name=days]").addEventListener("input", updateEstimate);

async function loadRentals() {
    const onlyActive = document.getElementById("onlyActive").checked;
    const rentals = await api("/rentals?active=" + onlyActive);
    document.getElementById("rentalTable").innerHTML = rentals.length
        ? rentals.map(r => `
            <tr>
                <td>${r.id}</td>
                <td>${escapeHtml(r.customer.name)}</td>
                <td>${r.vehicle.id} - ${escapeHtml(r.vehicle.brand)} ${escapeHtml(r.vehicle.model)}</td>
                <td>${r.days}</td>
                <td>${rs(r.totalCost)}</td>
                <td><span class="badge ${r.returned ? "grey" : "green"}">${r.returned ? "Returned" : "Active"}</span></td>
                <td>${r.returned ? "" : `<button class="small" onclick="returnVehicle('${r.id}')">Return</button>`}</td>
            </tr>`).join("")
        : `<tr><td colspan="7" class="muted">No rentals to show.</td></tr>`;
}

document.getElementById("onlyActive").addEventListener("change", loadRentals);

document.getElementById("rentalForm").addEventListener("submit", async e => {
    e.preventDefault();
    const f = e.target;
    try {
        const r = await api("/rentals", "POST", {
            customerId: f.customerId.value,
            vehicleId: f.vehicleId.value,
            days: Number(f.days.value)
        });
        showMessage(`Rental ${r.id} created for ${r.customer.name}. Total: ${rs(r.totalCost)}`);
        f.reset();
        refreshAll();
    } catch (err) {
        showMessage(err.message, "error");
    }
});

async function returnVehicle(id) {
    try {
        const r = await api(`/rentals/${id}/return`, "POST");
        showMessage(`${r.vehicle.brand} ${r.vehicle.model} returned. Thank you, ${r.customer.name}!`);
        refreshAll();
    } catch (err) {
        showMessage(err.message, "error");
    }
}

// ---------- init ----------
function refreshAll() {
    Promise.all([loadVehicles(), loadCustomers(), loadRentForm(), loadRentals()])
        .catch(err => showMessage("Could not reach server: " + err.message, "error"));
}

refreshAll();
