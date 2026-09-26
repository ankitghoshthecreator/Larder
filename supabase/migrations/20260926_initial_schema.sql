-- Supabase Initial Schema for Larder

create table if not exists households (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  created_at timestamptz default now()
);

create table if not exists household_members (
  household_id uuid references households(id) on delete cascade,
  user_id uuid references auth.users(id),
  role text default 'member' check (role in ('owner','member')),
  primary key (household_id, user_id)
);

create table if not exists items (
  id uuid primary key default gen_random_uuid(),
  household_id uuid not null references households(id),
  name text not null,
  category text not null,
  quantity numeric default 1,
  unit text default 'unit',
  expiry_estimate date,
  source_image_path text,
  status text default 'CONFIRMED' check (status in ('CONFIRMED', 'NEEDS_REVIEW')),
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

create table if not exists receipt_scans (
  id uuid primary key default gen_random_uuid(),
  household_id uuid not null references households(id),
  image_path text not null,
  raw_ocr_text text,
  status text default 'pending' check (status in ('pending','processed','failed')),
  created_at timestamptz default now()
);

alter table items enable row level security;
alter table receipt_scans enable row level security;
alter table household_members enable row level security;

create policy "household members can read/write items" on items
  for all using (
    household_id in (select household_id from household_members where user_id = auth.uid())
  );

create policy "household members can read/write scans" on receipt_scans
  for all using (
    household_id in (select household_id from household_members where user_id = auth.uid())
  );

create policy "members see their own household rows" on household_members
  for select using (user_id = auth.uid());
